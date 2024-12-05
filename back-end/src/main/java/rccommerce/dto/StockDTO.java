package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Stock;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockDTO {

    private Long id;
    private UserDTO user;
    private ProductDTO product;
    private BigDecimal quantity;
    private BigDecimal qttMoved;
    private Instant moment;
    private String moviment;

    public StockDTO(ProductDTO product, Instant moment, String moviment, BigDecimal qttMoved) {
        this.product = product;
        this.moment = moment;
        this.moviment = moviment;
        this.qttMoved = qttMoved;
    }

    public StockDTO(Stock entity) {
        id = entity.getId();
        user = new UserDTO(entity.getUser());
        product = new ProductDTO(entity.getProduct());
        quantity = entity.getQuantity();
        moment = entity.getMoment();
        qttMoved = entity.getQttMoved();
    }
}
