package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.CashMovement;

@AllArgsConstructor
@Getter
public class CashMovementDTO {

    @NotNull(message = "O ID não pode ser nulo.")
    private final Long id;

    @NotNull(message = "O tipo de movimento não pode ser nulo.")
    private final String cashMovementType;

    private final String paymentType;

    @NotNull(message = "O valor não pode ser nulo.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor deve ser maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private final BigDecimal amount;

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
    private final String description;

    @NotNull(message = "O timestamp não pode ser nulo.")
    private final Instant timestamp;

    @NotNull(message = "O ID do caixa não pode ser nulo.")
    private final Long cashRegisterId;

    public CashMovementDTO(CashMovement entity) {
        this.id = entity.getId();
        this.cashMovementType = entity.getCashMovementType().getName();
        this.paymentType = entity.getPaymentType().getName();
        this.amount = entity.getAmount();
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
        this.cashRegisterId = entity.getCashRegister().getId();
    }
}
