package rccommerce.dto;

import jakarta.validation.constraints.Positive;
import rccommerce.entities.OrderItem;

public class OrderItemDTO {

	@Positive(message = "Informe um valor positivo")
	private Long productId;
	private String name;	
	private Double price;
	
	@Positive(message = "Informe um valor positivo")
	private Double quantity;
	private String imgUrl;
	
	public OrderItemDTO(Long productId, String name, Double price, Double quantity, String imgUrl) {
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

	public Double getPrice() {
		return price;
	}

	public Double getQuantity() {
		return quantity;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}
	
	public Double getSubTotal() {
		return price * quantity;
	}
}
