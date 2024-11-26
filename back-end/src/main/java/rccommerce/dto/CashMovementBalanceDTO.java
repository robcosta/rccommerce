package rccommerce.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import rccommerce.entities.CashMovement;

@Builder
@AllArgsConstructor
@Getter
public class CashMovementBalanceDTO {

    private Long id;

    @NotNull(message = "O valor não pode ser nulo.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor deve ser maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private BigDecimal amount;

    public CashMovementBalanceDTO(CashMovement entity) {
        entity.setId(id);
        entity.setAmount(amount);
    }
}
