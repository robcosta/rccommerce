package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.CashRegister;

@AllArgsConstructor
@Getter
public class CashRegisterDTO {

    @NotNull(message = "O ID do caixa não pode ser nulo.")
    private final Long id;

    @NotNull(message = "O saldo não pode ser nulo.")
    @DecimalMin(value = "0.00", inclusive = true, message = "O saldo deve ser igual ou maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O saldo deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private final BigDecimal balance;

    private final Instant openTime;

    private final Instant closeTime;

    @NotNull(message = "O operador não pode ser nulo.")
    @Size(min = 1, message = "O operador deve ser especificado.")
    private final String operatorName;

    private final List<CashMovementDTO> movements;

    public CashRegisterDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        this.operatorName = entity.getOperator() != null ? entity.getOperator().getName() : null;
        this.movements = entity.getMovements() != null
                ? entity.getMovements().stream().map(CashMovementDTO::new).collect(Collectors.toList())
                : null;
    }
}
