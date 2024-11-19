package rccommerce.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import rccommerce.entities.OrderItem;

public class OrderItemDTO {

    @Positive(message = "Informe um valor positivo")
    private Long productId;
    private String name;
    private BigDecimal price;

    @Positive(message = "Informe um valor positivo")
    private BigDecimal quantity;
    private String imgUrl;

    public OrderItemDTO(Long productId, String name, BigDecimal price, BigDecimal quantity, String imgUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imgUrl = imgUrl;
    }

    public OrderItemDTO(OrderItem entity) {
        productId = entity.getProduct().getId();
        name = entity.getProduct().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
        imgUrl = entity.getProduct().getImgUrl();
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public BigDecimal getSubTotal() {
        return price.multiply(quantity);
    }
}
