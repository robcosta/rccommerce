package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.PaymentDTO;
import rccommerce.dto.PaymentDetailDTO;
import rccommerce.dto.PaymentMinDTO;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.StockDTO;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.PaymentDetail;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.PaymentType;
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
            PaymentDetail moneyPayment = entity.getPaymentDetails().stream()
                    .filter(detail -> detail.getPaymentType().equals(PaymentType.MONEY))
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

        // Processar pagamento exato
        order.setStatus(OrderStatus.PAID);

        // Atualiza o caixa
        for (PaymentDetail paymentDetail : entity.getPaymentDetails()) {
            CashMovementDTO casMovimentDto = CashMovementDTO.builder()
                    .cashMovementType("SALE")
                    .paymentType(paymentDetail.getPaymentType().getName())
                    .amount(paymentDetail.getAmount())
                    .description("Order: " + order.getId())
                    .timestamp(entity.getMoment())
                    .build();

            cashMovementService.insert(casMovimentDto);
        }

        updateStock(order, entity.getMoment());
        repository.save(entity);
        PaymentMinDTO paymentMinDTO = new PaymentMinDTO(entity);
        paymentMinDTO.setMessage(message.toString());

        return paymentMinDTO;
    }

    private void updateStock(Order order, Instant paymentMoment) {
        String moviment = StockMoviment.SALE.name();
        for (OrderItem item : order.getItens()) {
            ProductDTO product = new ProductDTO(item.getProduct());
            BigDecimal qttMoved = item.getQuantity();
            StockDTO dto = new StockDTO(product, paymentMoment, moviment, qttMoved);
            stockService.updateStock(dto);
        }
    }

    @Override
    public JpaRepository<Payment, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(PaymentDTO dto, Payment entity) {
        entity.setMoment(Instant.now());
        entity.setOrder(order);
        // Garantir que paymentDetails não seja nulo antes de processar
        if (dto.getPaymentDetails() == null || dto.getPaymentDetails().isEmpty()) {
            throw new InvalidArgumentExecption("A lista de detalhes de pagamento está vazia ou nula.");
        }

        // Validar os detalhes de pagamento para garantir que paymentType não seja nulo
        dto.getPaymentDetails().forEach(paymentDetailDTO -> {
            if (paymentDetailDTO.getPaymentType() == null) {
                throw new InvalidArgumentExecption("O tipo de pagamento não pode ser nulo.");
            }
            if (paymentDetailDTO.getAmount() == null || paymentDetailDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidArgumentExecption("O valor do pagamento deve ser maior que zero.");
            }
        });

        // Criar um mapa para agrupar e somar os valores por tipo de pagamento
        Map<PaymentType, BigDecimal> groupedPayments = dto.getPaymentDetails().stream()
                .collect(Collectors.toMap(
                        PaymentDetailDTO::getPaymentType,
                        PaymentDetailDTO::getAmount,
                        BigDecimal::add // Combina os valores de tipos iguais somando-os
                ));

        // Limpar detalhes de pagamento existentes na entidade
        entity.getPaymentDetails().clear();

        // Converter os valores agrupados em PaymentDetail e adicionar à entidade
        groupedPayments.forEach((paymentType, totalAmount) -> {
            PaymentDetail paymentDetail = new PaymentDetail(paymentType, totalAmount);
            entity.addPaymentDetail(paymentDetail);
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
