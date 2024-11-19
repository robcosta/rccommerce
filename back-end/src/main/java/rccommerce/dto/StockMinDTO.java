package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import rccommerce.entities.Stock;

public class StockMinDTO {

    private Long id;
    private UserDTO user;
    private ProductDTO product;
    private BigDecimal quantity;
    private BigDecimal qttMoved;
    private Instant moment;
    private String moviment;

    public StockMinDTO() {
    }

    public StockMinDTO(Long id, UserDTO user, ProductDTO product, BigDecimal quantity, BigDecimal qttMoved, Instant moment, String moviment) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.qttMoved = qttMoved;
        this.moment = moment;
        this.moviment = moviment;
    }

    public StockMinDTO(Stock entity) {
        id = entity.getId();
        user = new UserDTO(entity.getUser());
        product = new ProductDTO(entity.getProduct());
        quantity = entity.getQuantity();
        moment = entity.getMoment();
        qttMoved = entity.getQttMoved();
    }

    public Long getId() {
        return id;
    }

    public UserDTO getUser() {
        return user;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getQttMoved() {
        return qttMoved;
    }

    public Instant getMoment() {
        return moment;
    }

    public String getMoviment() {
        return moviment;
    }
}
