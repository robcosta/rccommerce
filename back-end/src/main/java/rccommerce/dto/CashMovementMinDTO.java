package rccommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.CashMovement;
import rccommerce.entities.MovementDetail;

@AllArgsConstructor
@Getter
public class CashMovementMinDTO {

    private final Long id;
    private List<MovementDetailDTO> movementDetails = new ArrayList<>();
    private final String description;
    private final Instant timestamp;
    private final CashRegisterDTO cahsRegisterDto;

    public CashMovementMinDTO(CashMovement entity) {
        this.id = entity.getId();
        for (MovementDetail movement : entity.getMovementDetails()) {
            this.getMovementDetails().add(new MovementDetailDTO(movement));
        }
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
        this.cahsRegisterDto = new CashRegisterDTO(entity.getCashRegister());
    }

    // public BigDecimal getTotalAmount() {
    //     return movementDetails.stream()
    //             .map(MovementDetailDTO::getAmount)
    //             .reduce(BigDecimal.ZERO, BigDecimal::add);
    // }
}
