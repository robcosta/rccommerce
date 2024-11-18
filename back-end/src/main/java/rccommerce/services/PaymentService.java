package rccommerce.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Payment payment = new Payment();

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        if (order.getPayment() != null) {
            throw new InvalidArgumentExecption("Pedido já pago");
        }

        payment.setMoment(Instant.now());
        payment.setPaymentType(PaymentType.fromValue(dto.getPaymentType()));
        // try {
        //     payment.setPaymentType(PaymentType.valueOf(dto.getPaymentType()));
        // } catch (IllegalArgumentException e) {
        //     throw new InvalidArgumentExecption("Tipo de pagamento inexistente: " + dto.getPaymentType());
        // }
        payment.setOrder(order);
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
        payment = repository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);
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
}
