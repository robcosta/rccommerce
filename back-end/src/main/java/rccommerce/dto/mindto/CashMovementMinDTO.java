package rccommerce.dto.mindto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import rccommerce.entities.CashMovement;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.MovementType;

@Getter
public class CashMovementMinDTO {

    private final Long id;
    private final CashMovementType cashMovementType;
    private final String description;
    private final Instant timestamp;
    private List<MovementDetailMinDTO> movementDetails = new ArrayList<>();

    public CashMovementMinDTO(CashMovement entity) {
        this.id = entity.getId();
        this.cashMovementType = entity.getCashMovementType();
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
        this.movementDetails = entity.getMovementDetails().stream()
                .map(MovementDetailMinDTO::new)
                .collect(Collectors.toList());
    }

    public CashMovementMinDTO(CashMovement entity, MovementType filterType) {
        this.id = entity.getId();
        this.cashMovementType = entity.getCashMovementType();
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();

        // Aplica o filtro, se especificado
        this.movementDetails = entity.getMovementDetails().stream()
                .filter(movementDetail -> filterType == null || movementDetail.getMovementType() == filterType)
                .map(MovementDetailMinDTO::new)
                .collect(Collectors.toList());

    }

    public BigDecimal getTotalAmount() {
        return movementDetails.stream()
                .map(MovementDetailMinDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
