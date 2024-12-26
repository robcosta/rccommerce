package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import rccommerce.entities.Payment;
import rccommerce.entities.enums.MovementType;

@Builder
@AllArgsConstructor
@Getter
public class PaymentDTO {

    private final Long id;
    private final Instant moment;

    @NotNull(message = "O valor não pode ser nulo.")
    private final Long orderId;

    @NotNull(message = "O valor não pode ser nulo.")
    @Valid // Valida cada elemento da lista de acordo com as regras em MovementDetailDTO
    private final CashRegisterDTO cashRegister;

    public PaymentDTO(Payment entity) {
        this.id = entity.getId();
        this.orderId = entity.getOrder().getId();
        this.cashRegister = new CashRegisterDTO(entity.getCashRegister());
        this.moment = entity.getMoment();
    }

    public BigDecimal getTotalPayments() {
        return cashRegister.getCashMovements().stream()
                .flatMap(cashMovement -> cashMovement.getMovementDetails().stream())
                .map(MovementDetailDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalMoneyPayments() {
        return cashRegister.getCashMovementDTO().stream()
                .flatMap(movements -> movements.getMovementDetails().stream()
                .filter(detail -> MovementType.MONEY.equals(detail.getMovementType())))
                .map(MovementDetailDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
