package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.CashRegister;

@AllArgsConstructor
@Getter
public class CashRegisterDTO {

    private Long id;

    @DecimalMin(value = "0.00", inclusive = true, message = "O saldo deve ser igual ou maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O saldo deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private BigDecimal balance;

    private Instant openTime;

    private Instant closeTime;

    @Size(min = 1, message = "O operador deve ser especificado.")
    private String operatorName;

    @NotEmpty(message = "A lista de pagamentos não pode ser nula ou vazia.")
    @Valid // Valida cada elemento da lista de acordo com as regras em MovementDetailDTO
    private List<CashMovementDTO> cashMovements = new ArrayList<>();

    public CashRegisterDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        this.operatorName = entity.getOperator() != null ? entity.getOperator().getName() : null;
        this.cashMovements = entity.getMovements() != null
                ? entity.getMovements().stream().map(CashMovementDTO::new).collect(Collectors.toList())
                : null;
    }
}
