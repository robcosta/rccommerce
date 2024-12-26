package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.MovementDetailDTO;
import rccommerce.dto.PaymentDTO;
import rccommerce.dto.PaymentMinDTO;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.StockDTO;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.MovementType;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.PaymentRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;

@Service
public class PaymentService implements GenericService<Payment, PaymentDTO, PaymentMinDTO, Long> {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private CashMovementService cashMovementService;

    @Autowired
    private MessageSource messageSource;

    private Order order;

    @Override
    @Transactional
    public PaymentMinDTO insert(PaymentDTO dto) {
        StringBuilder message = new StringBuilder("Pagamento Concluído.");
        order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        if (order.getPayment() != null) {
            throw new InvalidArgumentExecption("Pedido de Id: '" + dto.getOrderId() + "' já foi pago.");
        }

        Payment entity = createEntity();
        copyDtoToEntity(dto, entity);

        // Verificar se o valor total pago cobre o valor do pedido
        BigDecimal totalPayments = entity.getTotalPayments();
        BigDecimal totalOrder = order.getTotalOrder();

        if (totalPayments.compareTo(totalOrder) > 0) {
            // Valor excedente
            BigDecimal excessAmount = totalPayments.subtract(totalOrder);

            // Verificar se há pagamento em dinheiro
            MovementDetail moneyPayment = entity.getMovementDetails().stream()
                    .filter(detail -> detail.getMovementType().equals(MovementType.MONEY))
                    .findFirst()
                    .orElse(null);

            if (moneyPayment == null || moneyPayment.getAmount().compareTo(excessAmount) < 0) {
                BigDecimal money = new BigDecimal(0.00);
                if (moneyPayment != null) {
                    money = money.add(moneyPayment.getAmount());
                }
                throw new InvalidArgumentExecption(String.format("Pagamento não concluído. Valor pago de R$ %.2f excede o valor do pedido R$ %.2f. "
                        + "Troco de R$ %.2f só pode ser devolvido em dinheiro, contudo a parcela em dinheiro (R$ %.2f) recebida é inferior ao troco.",
                        totalPayments, totalOrder, totalPayments.subtract(totalOrder), money));
            }

            // Ajustar o valor em dinheiro para refletir o troco
            moneyPayment.setAmount(moneyPayment.getAmount().subtract(excessAmount));
            message.append(String.format(" Troco: R$ %.2f", excessAmount));
        }

        // Pagamento exato ou insuficiente
        if (totalPayments.compareTo(totalOrder) < 0) {
            throw new InvalidArgumentExecption(
                    String.format("Valor pago: R$ %.2f é insuficiente para cobrir o total do pedido: R$ %.2f.", totalPayments, totalOrder));
        }

        // Atualiza o pedido
        updateOrder(order);

        //Atualiza o estoque
        updateStock(order, entity.getMoment());

        //Salva o pagamento
        try {
            entity = repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
        }

        dto = new PaymentDTO(entity);

        // Atualiza o caixa        
        updateCash(dto);

        PaymentMinDTO paymentMinDTO = new PaymentMinDTO(entity);
        paymentMinDTO.setMessage(message.toString());

        return paymentMinDTO;
    }

    //Altera o Status do pedido
    private void updateOrder(Order order) {
        order.setStatus(OrderStatus.PAID);
        try {
            orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
        }
    }

    // Atualiza o estoque
    private void updateStock(Order order, Instant paymentMoment) {
        String moviment = StockMoviment.SALE.name();
        for (OrderItem item : order.getItens()) {
            ProductDTO product = new ProductDTO(item.getProduct());
            BigDecimal qttMoved = item.getQuantity();
            StockDTO dto = new StockDTO(product, paymentMoment, moviment, qttMoved);
            stockService.updateStock(dto);
        }
    }

    //Atualiza o caixa
    private void updateCash(PaymentDTO dto) {

        CashMovementDTO cashMovementDTO = CashMovementDTO.builder()
                .cashMovementType(CashMovementType.SALE.getName())
                .movementDetails(dto.getMovementDetails())
                .build();

        cashMovementService.inputBalance(cashMovementDTO);
    }

    @Override
    public JpaRepository<Payment, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(PaymentDTO dto, Payment entity) {
        entity.setMoment(Instant.now());
        entity.setOrder(order);

        // Validar os detalhes de pagamento para garantir que movementType não seja nulo
        dto.getMovementDetails().forEach(movementDetailDTO -> {
            if (movementDetailDTO.getMovementType() == null) {
                throw new InvalidArgumentExecption("O tipo de pagamento não pode ser nulo.");
            }
            if (movementDetailDTO.getAmount() == null || movementDetailDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidArgumentExecption("O valor do pagamento deve ser maior que zero.");
            }
        });

        // Criar um mapa para agrupar e somar os valores por tipo de pagamento
        Map<MovementType, BigDecimal> groupedPayments = dto.getMovementDetails().stream()
                .collect(Collectors.toMap(
                        MovementDetailDTO::getMovementType,
                        MovementDetailDTO::getAmount,
                        BigDecimal::add // Combina os valores de tipos iguais somando-os
                ));

        // Limpar detalhes de pagamento existentes na entidade
        entity.getMovementDetails().clear();

        // Converter os valores agrupados em MovementDetail e adicionar à entidade
        groupedPayments.forEach((movementType, totalAmount) -> {
            MovementDetail movementDetail = MovementDetail.builder()
                    .movementType(movementType)
                    .amount(totalAmount)
                    .build();
            entity.addPaymentDetail(movementDetail);
        });
    }

    @Override
    public Payment createEntity() {
        return new Payment();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.Payment", null, Locale.getDefault());
    }
}
