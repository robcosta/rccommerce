package rccommerce.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Value;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.enums.MovementType;

@Getter
@Value
public class MovementDetailMinDTO {

    private Long id;
    private MovementType movementType;
    private BigDecimal amount;

    public MovementDetailMinDTO(MovementDetail movementDetail) {
        this.id = movementDetail.getId();
        this.movementType = movementDetail.getMovementType();
        this.amount = movementDetail.getAmount();
    }
}
