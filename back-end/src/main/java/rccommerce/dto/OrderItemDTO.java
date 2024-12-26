package rccommerce.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.OrderItem;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@Getter
@Value
public class OrderItemDTO {

    @Positive(message = "Informe um valor positivo")
    private Long productId;
    private String name;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal price;

    @Positive(message = "Informe um valor positivo")
    private BigDecimal quantity;
    private String imgUrl;

    public OrderItemDTO(OrderItem entity) {
        productId = entity.getProduct().getId();
        name = entity.getProduct().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
        imgUrl = entity.getProduct().getImgUrl();
    }

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getSubTotal() {
        return price.multiply(quantity);
    }
}
