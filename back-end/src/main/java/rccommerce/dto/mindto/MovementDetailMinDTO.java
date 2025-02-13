package rccommerce.dto.mindto;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public BigDecimal getAmount() {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
