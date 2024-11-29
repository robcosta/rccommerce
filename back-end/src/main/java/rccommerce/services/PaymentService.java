package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import rccommerce.dto.PaymentDTO;
import rccommerce.dto.PaymentMinDTO;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.Stock;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.PaymentType;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.PaymentRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Service
public class PaymentService implements GenericService<Payment, PaymentDTO, PaymentMinDTO, Long> {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    // @Autowired
    // private StockService stockService;
    @Autowired
    private MessageSource messageSource;

    // @Transactional(readOnly = true)
    // public PaymentDTO findById(Long id) {
    //     Payment result = repository.findById(id)
    //             .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
    //     return new PaymentDTO(result);
    // }
    // @Override
    // @Transactional
    // public PaymentMinDTO insert(PaymentDTO dto) {
    //     Order order = orderRepository.getReferenceById(dto.getOrderId());
    //     if (order == null) {
    //         throw new ResourceNotFoundException("Pedido de Id:' " + dto.getOrderId() + "' não encontrado");
    //     }
    //     if (order.getPayment() != null) {
    //         throw new InvalidArgumentExecption("Pedido de Id:' " + dto.getOrderId() + "' já pago");
    //     }
    //    // List<Stock> productStock = new ArrayList<>();
    //     for (OrderItem item : order.getItens()) {
    //         item.
    //     }
    //     order.setStatus(OrderStatus.PAID);
    //     List<Stock> productStock = new ArrayList<>();
    //     return null;
    // }
    @Override
    @Transactional
    public PaymentMinDTO insert(PaymentDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        if (order.getPayment() != null) {
            throw new InvalidArgumentExecption("Pedido já pago");
        }

        if (dto.getAmount().compareTo(getAmountOrder(order)) < 0) {
            throw new InvalidArgumentExecption("Valor informado abaixo do valor do pedido.");
        }

        Payment payment = Payment.builder()
                .moment(Instant.now())
                .paymentType(PaymentType.fromValue(dto.getPaymentType()))
                .amount(dto.getAmount())
                .order(order)
                .build();

        order.setStatus(OrderStatus.PAID);
        List<Stock> productStock = new ArrayList<>();
        for (OrderItem item : order.getItens()) {
            Stock stock = new Stock();
            stock.setUser(order.getUser());
            stock.setProduct(item.getProduct());
            stock.setMoment(payment.getMoment());
            stock.setMoviment(StockMoviment.SALE);
            stock.setQuantity(item.getProduct().getQuantity());
            stock.setQttMoved(item.getQuantity());
            productStock.add(stock);
        }
        payment = repository.saveAndFlush(payment);
        order.setPayment(payment);
        orderRepository.saveAndFlush(order);
        productService.updateStock(productStock);
        return new PaymentMinDTO(payment);
    }

    @Override
    public JpaRepository<Payment, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(PaymentDTO dto, Payment entity) {

    }

    @Override
    public Payment createEntity() {
        return new Payment();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.Payment", null, Locale.getDefault());
    }

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getAmountOrder(Order order) {
        BigDecimal sum = BigDecimal.valueOf(0.0);
        for (OrderItem item : order.getItens()) {
            sum = sum.add(item.getPrice().multiply(item.getQuantity()));
        }
        return sum;
    }
}
