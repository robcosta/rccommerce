package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.MovementDetailDTO;
import rccommerce.dto.PaymentDTO;
import rccommerce.dto.PaymentMinDTO;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.Stock;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.PaymentRepository;
import rccommerce.repositories.StockRepository;
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
    private StockRepository stockRepository;

    @Autowired
    private CashRegisterService cashRegisterService;

    @Autowired
    private MessageSource messageSource;

    private Order order;

    private CashRegister cashRegister;

    private Instant moment;

    private StringBuilder message;

    @Transactional
    public PaymentMinDTO insertPayment(PaymentDTO dto) {
        moment = Instant.now();
        message = new StringBuilder("Pagamento Concluído.");

        // Verifica se o operador tem caixa aberto e realiza as tratativas do caixa
        updateCash(dto);

        // Verifica se o pedido existe
        order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        //Verifica se opedido já foi pago
        if (order.getPayment() != null) {
            throw new InvalidArgumentExecption("Pedido de Id: '" + dto.getOrderId() + "' já foi pago.");
        }

        //Analisa as formas de pagamento indicando troco, caso tenha e atualiza a mensagem
        paymentAnalysis(dto, order);

        // Atualiza o pedido para PAGO e o estoque
        updateOrderAndStock(order, moment);

        //Salva o pagamento
        PaymentMinDTO minDTO = insert(dto);
        minDTO.setMessage(message.toString());
        return minDTO;
    }

    //Atualiza o caixa
    private void updateCash(PaymentDTO dto) {
        // Verifica se exite caixa aberto para o operador
        cashRegister = cashRegisterService.validateOpenCashRegister();
        CashMovement cashMovement = new CashMovement();
        cashMovement.setCashMovementType(CashMovementType.SALE);
        for (CashMovementDTO cashMovementDTD : dto.getCashRegister().getCashMovements()) {
            for (MovementDetailDTO movementDetailDTO : cashMovementDTD.getMovementDetails()) {
                MovementDetail detail = new MovementDetail();
                Payment payment = new Payment();
                payment.setId(dto.getId());
                detail.setAmount(movementDetailDTO.getAmount());
                detail.setMovementType(movementDetailDTO.getMovementType());
                detail.setPayment(payment);
                cashMovement.addMovementDetail(detail);
            }
            cashRegister.addMovement(cashMovement);
        }
        cashRegisterService.insert(new CashRegisterDTO(cashRegister));
    }

    // Aanlisa as formas de pagamento indicando troco, caso tenha
    private void paymentAnalysis(PaymentDTO dto, Order order) {
        BigDecimal totalPayments = dto.getCashRegister().getTotalAmount();
        BigDecimal totalMoney = dto.getCashRegister().getTotalMoneyPayments();
        BigDecimal totalOrder = order.getTotalOrder();

        // Lança exceção caso o valor informado seja inferior ao valor do pedido
        if (totalPayments.compareTo(totalOrder) < 0) {
            throw new InvalidArgumentExecption(String.format("Pagamento não concluído. Valor pago de R$ %.2f é "
                    + "insuficiente para cobrir o total do pedido: R$ %.2f.", totalPayments, totalOrder));
        }

        // Valor excedente
        BigDecimal excessAmount = totalPayments.subtract(totalOrder);

        // Lança exceção caso o valor informado em dinheiro seja inferior ao troco
        if (totalMoney.compareTo(excessAmount) < 0) {
            throw new InvalidArgumentExecption(String.format("Pagamento não concluído. Valor pago de R$ %.2f excede"
                    + " o valor do pedido R$ %.2f. Troco de R$ %.2f só pode ser devolvido em dinheiro, contudo a "
                    + "parcela em dinheiro (R$ %.2f) recebida é inferior ao troco.",
                    totalPayments, totalOrder, totalPayments.subtract(totalOrder), totalMoney));
        }

        // Ajustar o valor em dinheiro para refletir o troco
        message.append(String.format(" Troco: R$ %.2f", excessAmount));
    }

    // Altera o Status do pedido para PAGO e atualiza o estoque
    @Transactional
    private void updateOrderAndStock(Order order, Instant moment) {

        // Atualiza o estoque
        for (OrderItem item : order.getItens()) {
            try {
                stockRepository.save(Stock.builder()
                        .product(item.getProduct())
                        .moment(moment)
                        .moviment(StockMoviment.SALE)
                        .quantity(item.getQuantity())
                        .build());
            } catch (DataIntegrityViolationException e) {
                handleDataIntegrityViolation(e);
            }

        }

        // Atualiza o status do pedido
        order.setStatus(OrderStatus.PAID);
        try {
            orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
        }
    }

    @Override
    public JpaRepository<Payment, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(PaymentDTO dto, Payment entity) {
        entity.setMoment(moment);
        entity.setOrder(order);
        entity.setCashRegister(cashRegister);
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
