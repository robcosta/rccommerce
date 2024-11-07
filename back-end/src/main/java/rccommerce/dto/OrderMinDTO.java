package rccommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.enums.OrderStatus;

public class OrderMinDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private UserOrderDTO User;

    private ClientOrderDTO client;
    private PaymentDTO payment;

    @NotEmpty(message = "Deve ter pelo menos um item")
    private List<OrderItemDTO> itens = new ArrayList<>();

    public OrderMinDTO(Long id, Instant moment, OrderStatus status, UserOrderDTO User, ClientOrderDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.User = User;
        this.client = client;
        this.payment = payment;
    }

    public OrderMinDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        status = entity.getStatus();
        User = new UserOrderDTO(entity.getUser());
        client = new ClientOrderDTO(entity.getClient());
        payment = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        for (OrderItem item : entity.getItens()) {
            itens.add(new OrderItemDTO(item));
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public UserOrderDTO getUser() {
        return User;
    }

    public ClientOrderDTO getClient() {
        return client;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public List<OrderItemDTO> getItens() {
        return itens;
    }

    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : itens) {
            sum += item.getSubTotal();
        }
        return sum;
    }
}
