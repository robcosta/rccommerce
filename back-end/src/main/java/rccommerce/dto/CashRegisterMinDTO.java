package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.CashRegister;
import rccommerce.entities.enums.MovementType;

@AllArgsConstructor
@Getter
@Value
public class CashRegisterMinDTO {

    private Long id;
    private BigDecimal balance;
    private Instant openTime;
    private Instant closeTime;
    private List<CashMovementMinDTO> cashMovements = new ArrayList<>();

    public CashRegisterMinDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        for (CashMovementMinDTO cashMovementDTO : entity.getCashMovements().stream().map(CashMovementMinDTO::new).collect(Collectors.toList())) {
            cashMovements.add(cashMovementDTO);
        }
    }

    public BigDecimal getTotalAmount() {
        return cashMovements.stream()
                .flatMap(cashMovementDTO -> cashMovementDTO.getMovementDetails().stream())
                .map(MovementDetailMinDTO::getAmount)
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
