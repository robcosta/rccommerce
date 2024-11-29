package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.CashMovement;

@AllArgsConstructor
@Getter
public class CashMovementMinDTO {

    private final Long id;
    private final String cashMovementType;
    private final BigDecimal amount;
    private final String description;
    private final Instant timestamp;

    public CashMovementMinDTO(CashMovement entity) {
        this.id = entity.getId();
        this.cashMovementType = entity.getCashMovementType().getName();
        this.amount = entity.getAmount();
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
    }
}
