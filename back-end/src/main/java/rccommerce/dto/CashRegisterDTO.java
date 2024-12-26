package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.CashRegister;
import rccommerce.entities.enums.MovementType;

@Builder
@AllArgsConstructor
@Getter
@Value
public class CashRegisterDTO {

    private Long id;

    private BigDecimal balance;

    private Instant openTime;

    private Instant closeTime;

    private String operatorName;

    @Builder.Default
    @NotEmpty(message = "A lista de pagamentos não pode ser nula ou vazia.")
    @Valid // Valida cada elemento da lista de acordo com as regras em MovementDetailDTO
    private List<CashMovementDTO> cashMovements = new ArrayList<>();

    //@NotNull(message = "O valor de forceClose não pode ser nulo.")
    private boolean forceClose;

    public CashRegisterDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        this.cashMovements = new ArrayList<>();
        this.operatorName = entity.getOperator() != null ? entity.getOperator().getName() : null;
        for (CashMovementDTO cashMovementDTO : entity.getCashMovements().stream().map(CashMovementDTO::new).collect(Collectors.toList())) {
            cashMovements.add(cashMovementDTO);
        }
        this.forceClose = false; // Default value
    }

    public BigDecimal getTotalAmount() {
        return cashMovements.stream()
                .flatMap(cashMovementDTO -> cashMovementDTO.getMovementDetails().stream())
                .map(MovementDetailDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<MovementType, BigDecimal> getMovementTotals() {
        Map<MovementType, BigDecimal> movementTotals = new EnumMap<>(MovementType.class);

        if (cashMovements != null) {
            cashMovements.stream()
                    .flatMap(cashMovementDTO -> cashMovementDTO.getMovementDetails().stream())
                    .forEach(detail -> {
                        MovementType type = detail.getMovementType();
                        BigDecimal amount = detail.getAmount();

                        // Adiciona o valor ao total acumulado ou inicializa se ainda não existe
                        movementTotals.merge(type, amount, BigDecimal::add);
                    });
        }

        // Remove entradas com total igual a zero
        movementTotals.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);

        return movementTotals;
    }

}
