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
public class StockMinDTO {

    private Long id;
    private UserDTO user;
    private ProductDTO product;
    private BigDecimal quantity;
    private BigDecimal qttMoved;
    private Instant moment;
    private String moviment;

    public StockMinDTO(Stock entity) {
        id = entity.getId();
        user = new UserDTO(entity.getUser());
        product = new ProductDTO(entity.getProduct());
        quantity = entity.getQuantity();
        moment = entity.getMoment();
        qttMoved = entity.getQttMoved();
    }
}
