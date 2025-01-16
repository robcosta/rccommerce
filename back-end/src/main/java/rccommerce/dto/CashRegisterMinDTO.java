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
import rccommerce.entities.CashRegister;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.MovementType;

@AllArgsConstructor
@Getter
public class CashRegisterMinDTO {

    private final Long id;
    private final BigDecimal balance;
    private final Instant openTime;
    private final Instant closeTime;
    private List<CashMovementMinDTO> cashMovements = new ArrayList<>();

    public CashRegisterMinDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        this.cashMovements = entity.getCashMovements().stream()
                .map(CashMovementMinDTO::new)
                .collect(Collectors.toList());
    }

    // Construtor com filtro de CashMovementType
    public CashRegisterMinDTO(CashRegister entity, CashMovementType filterCashMovement) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();

        // Aplica o filtro, se especificado
        this.cashMovements = entity.getCashMovements().stream()
                .filter(cashMovement -> filterCashMovement == null || cashMovement.getCashMovementType() == filterCashMovement)
                .map(CashMovementMinDTO::new)
                .collect(Collectors.toList());
    }

    // Construtor com filtro de MovementType
    public CashRegisterMinDTO(CashRegister entity, MovementType filterMovementType) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();

        // Aplica os filtros para CashMovements e MovementDetails, se especificados
        this.cashMovements = entity.getCashMovements().stream()
                .map(cashMovement -> new CashMovementMinDTO(cashMovement, filterMovementType)) // Aplica filtro no MovementType no DTO
                .collect(Collectors.toList());
    }

    // Construtor com filtro de CashMovementType e MovementType
    public CashRegisterMinDTO(CashRegister entity, CashMovementType filterCashMovement, MovementType filterMovementType) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();

        // Aplica os filtros para CashMovements e MovementDetails, se especificados
        this.cashMovements = entity.getCashMovements().stream()
                .filter(cashMovement -> filterCashMovement == null || cashMovement.getCashMovementType() == filterCashMovement) // Filtro pelo CashMovementType
                .map(cashMovement -> new CashMovementMinDTO(cashMovement, filterMovementType)) // Aplica filtro no MovementType no DTO
                .collect(Collectors.toList());
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
