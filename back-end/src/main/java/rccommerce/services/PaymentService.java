package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.MovementDetailDTO;
import rccommerce.dto.PaymentDTO;
import rccommerce.dto.mindto.PaymentMinDTO;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.ProductStock;
import rccommerce.entities.User;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.MovementType;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.StockMovement;
import rccommerce.repositories.CashRegisterRepository;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.PaymentRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class PaymentService implements GenericService<Payment, PaymentDTO, PaymentMinDTO, Long> {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    @Autowired
    private MessageSource messageSource;

    private StringBuilder message;

    private BigDecimal moneyPayment; // Valor em dinheiro

    @Transactional
    public PaymentMinDTO insertPayment(PaymentDTO dto) {
        // Mensagem para o usuário informando que o pagamento foi concluído e se tem troco
        message = new StringBuilder("Pagamento Concluído.");
        Payment entity = createEntity();

        // Atualiza, inclusive o pedido e o estoque
        copyDtoToEntity(dto, entity);
        try {
            entity = getRepository().save(entity);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }

        //Atualiza o caixa
        updateCash(entity, dto, moneyPayment);
        //Salva o pagamento
        PaymentMinDTO minDTO = entity.convertMinDTO();
        minDTO.setMessage(message.toString());
        return minDTO;
    }

    // Altera o Status do pedido para PAGO e atualiza o estoque
    @Transactional
    private Order updateOrderAndStock(PaymentDTO dto, Instant moment) {
        // Verifica se o pedido existe
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        //Verifica se opedido já foi pago
        if (order.getPayment() != null) {
            throw new InvalidArgumentExecption("Pedido de Id: '" + dto.getOrderId() + "' já foi pago.");
        }

        // Busca o usuário logado
        User user = userRepository.getReferenceById(SecurityContextUtil.getUserId());

        //Salva o pedido e atualiza o estoque
        try {
            for (OrderItem item : order.getItens()) {
                // Atualiza o estoque do produto
                ProductStock stock = ProductStock.builder()
                        .user(user)
                        .product(item.getProduct())
                        .quantity(item.getProduct().getQuantity())
                        .moment(moment)
                        .movement(StockMovement.SALE)
                        .build();
                stock.setQttMoved(item.getQuantity());

                stockRepository.save(stock);
            }
            order.setStatus(OrderStatus.PAID);
            // Atualiza o estatus do pedido
            orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
        }
        return order;
    }

    //Atualiza o caixa
    @Transactional
    private void updateCash(Payment entity, PaymentDTO dto, BigDecimal moneyPayment) {
        CashRegister cashRegister = entity.getCashRegister();
        CashMovement cashMovement = new CashMovement();
        cashMovement.setCashMovementType(CashMovementType.SALE);
        cashMovement.setTimestamp(entity.getMoment());
        cashMovement.setDescription(CashMovementType.SALE.getDescription() + " - Pedido: " + entity.getOrder().getId() + " - Pagamento: " + entity.getId());
        for (CashMovementDTO cashMovementDTD : dto.getCashRegister().getCashMovements()) {
            for (MovementDetailDTO movementDetailDTO : cashMovementDTD.getMovementDetails()) {
                cashMovement.addMovementDetail(MovementDetail.builder()
                        .amount((moneyPayment != null && movementDetailDTO.getMovementType().equals(MovementType.MONEY))
                                ? moneyPayment
                                : movementDetailDTO.getAmount())
                        .movementType(movementDetailDTO.getMovementType())
                        .payment(entity)
                        .build());
            }
            cashRegister.addMovement(cashMovement);
        }
        cashRegisterRepository.save(cashRegister);
    }

    // Analisa as formas de pagamento retornando a aparte em dinheiro e indicando o troco, caso tenha.
    private BigDecimal paymentAnalysis(PaymentDTO dto, Order order) {
        BigDecimal totalPayments = dto.getCashRegister().getTotalAmount();
        BigDecimal totalMoney = dto.getCashRegister().getTotalMoneyPayments();
        BigDecimal totalOrder = order.getTotalOrder();

        // Lança exceção caso o valor informado seja inferior ao valor do pedido
        if (totalPayments.compareTo(totalOrder) < 0) {
            throw new InvalidArgumentExecption(("Pagamento não concluído. Valor informado de R$ %.2f é "
                    + "insuficiente para cobrir o total do pedido: R$ %.2f.").formatted(totalPayments, totalOrder));
        }

        // Valor excedente
        BigDecimal excessAmount = totalPayments.subtract(totalOrder);

        // Lança exceção caso o valor informado em dinheiro seja inferior ao troco
        if (totalMoney.compareTo(excessAmount) < 0) {
            throw new InvalidArgumentExecption(("Pagamento não concluído. Valor informado R$ %.2f excede"
                    + " o valor do pedido R$ %.2f. Troco de R$ %.2f só pode ser devolvido em dinheiro, contudo a "
                    + "parcela em dinheiro (R$ %.2f) recebida é inferior ao troco.").formatted(
                            totalPayments, totalOrder, totalPayments.subtract(totalOrder), totalMoney));
        }

        // Ajustar o valor em dinheiro para refletir o troco
        message.append(" Troco: R$ %.2f".formatted(excessAmount));

        return totalMoney.subtract(excessAmount);
    }

    @Override
    public JpaRepository<Payment, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(PaymentDTO dto, Payment entity) {
        Instant moment = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Order order = updateOrderAndStock(dto, moment);
        moneyPayment = paymentAnalysis(dto, order);

        entity.setMoment(moment);
        entity.setOrder(order);
        entity.setCashRegister(validateOpenCashRegister());
    }

    @Transactional(readOnly = true)
    public CashRegister validateOpenCashRegister() {
        List<CashRegister> cashRegisters = cashRegisterRepository.findByOperatorId(getOperatorId());
        return cashRegisters.stream()
                .filter(cashRegister -> cashRegister.getCloseTime() == null)
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentExecption("O operador não possui um caixa aberto."));
    }

    private Long getOperatorId() {
        return SecurityContextUtil.getUserId();
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
