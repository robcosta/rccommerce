package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Value;
import rccommerce.entities.CashMovement;
import rccommerce.entities.MovementDetail;

@Getter
@Value
public class CashMovementMinDTO {

    private Long id;
    private List<MovementDetailMinDTO> movementDetails = new ArrayList<>();
    private String description;
    private Instant timestamp;

    public CashMovementMinDTO(CashMovement entity) {
        this.id = entity.getId();
        for (MovementDetail movementDetail : entity.getMovementDetails()) {
            getMovementDetails().add(new MovementDetailMinDTO(movementDetail));
        }
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
    }

    public BigDecimal getTotalAmount() {
        return movementDetails.stream()
                .map(MovementDetailMinDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
