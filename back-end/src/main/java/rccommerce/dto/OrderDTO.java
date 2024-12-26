package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@Getter
@Value
public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private UserOrderDTO User;
    private ClientOrderDTO client;
    private PaymentDTO payment;

    @Builder.Default
    @NotEmpty(message = "Deve ter pelo menos um item")
    @Valid
    private List<OrderItemDTO> itens = new ArrayList<>();

    public OrderDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        status = entity.getStatus();
        User = new UserOrderDTO(entity.getUser());
        client = new ClientOrderDTO(entity.getClient());
        payment = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        itens = new ArrayList<>();
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

    // Consolida itens duplicados somando suas quantidades
    public List<OrderItemDTO> getItens() {
        Map<Long, OrderItemDTO> itemMap = new HashMap<>();

        for (OrderItemDTO item : itens) {
            if (itemMap.containsKey(item.getProductId())) {
                OrderItemDTO existingItem = itemMap.get(item.getProductId());
                BigDecimal newQuantity = existingItem.getQuantity().add(item.getQuantity());
                itemMap.put(item.getProductId(), OrderItemDTO.builder()
                        .productId(existingItem.getProductId())
                        .name(existingItem.getName())
                        .price(existingItem.getPrice())
                        .quantity(newQuantity)
                        .imgUrl(existingItem.getImgUrl())
                        .build());
            } else {
                itemMap.put(item.getProductId(), item);
            }
        }

        return new ArrayList<>(itemMap.values());
    }
}
