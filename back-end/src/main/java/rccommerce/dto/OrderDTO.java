package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@AllArgsConstructor
@Getter
public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private UserOrderDTO User;
    private ClientOrderDTO client;
    private PaymentDTO payment;

    @NotEmpty(message = "Deve ter pelo menos um item")
    private List<OrderItemDTO> itens = new ArrayList<>();

    public OrderDTO(Order entity) {
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

    //Retorna o valor total do pedido
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getTotal() {
        return itens.stream()
                .map(item -> item.getPrice().multiply(item.getQuantity())) // Multiplica preço pela quantidade
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma os resultados
    }
}
