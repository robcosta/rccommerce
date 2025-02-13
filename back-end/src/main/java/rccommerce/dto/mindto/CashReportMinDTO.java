package rccommerce.dto.mindto;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import rccommerce.entities.CashRegister;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.MovementType;

@Getter
public class CashReportMinDTO {

    Set<CashRegisterMinDTO> cashRegisters = new HashSet<>();

    public CashReportMinDTO(Set<CashRegister> cashRegisters) {
        // Inicializa o campo com a transformação das entidades para DTOs filtrando os movimentos de tipo SALE
        this.cashRegisters = (cashRegisters != null)
                ? cashRegisters.stream()
                        .map(cashRegister -> new CashRegisterMinDTO(cashRegister))
                        .collect(Collectors.toSet())
                : new HashSet<>(); // Inicializa como vazio se o conjunto recebido for nulo        
    }

    public CashReportMinDTO(Set<CashRegister> cashRegisters, CashMovementType filterCashMovement) {
        this.cashRegisters = (cashRegisters != null)
                ? cashRegisters.stream()
                        .filter(cashRegister -> cashRegister.getCashMovements() != null && !cashRegister.getCashMovements().isEmpty())
                        .map(cashRegister -> new CashRegisterMinDTO(cashRegister, filterCashMovement))
                        .collect(Collectors.toSet())
                : new HashSet<>(); // Inicializa como vazio se o conjunto recebido for nulo

    }

    public CashReportMinDTO(Set<CashRegister> cashRegisters, MovementType filterMovementType) {
        this.cashRegisters = (cashRegisters != null)
                ? cashRegisters.stream()
                        .map(cashRegister -> new CashRegisterMinDTO(cashRegister, filterMovementType))
                        .collect(Collectors.toSet())
                : new HashSet<>(); // Inicializa como vazio se o conjunto recebido for nulo
    }

    public CashReportMinDTO(Set<CashRegister> cashRegisters, CashMovementType filterCashMovement, MovementType filterMovementType) {
        this.cashRegisters = (cashRegisters != null)
                ? cashRegisters.stream()
                        .map(cashRegister -> new CashRegisterMinDTO(cashRegister, filterCashMovement, filterMovementType))
                        .collect(Collectors.toSet())
                : new HashSet<>(); // Inicializa como vazio se o conjunto recebido for nulo
    }

    public Set<CashRegisterMinDTO> getCashRegisters() {
        Set<CashRegisterMinDTO> result = cashRegisters.stream()
                .filter(cashRegister -> !cashRegister.getTotalAmount().equals(BigDecimal.ZERO))
                .collect(Collectors.toSet());
        return result;
    }

    public Map<MovementType, BigDecimal> getTotalizationCash() {
        Map<MovementType, BigDecimal> totalizationCash = new EnumMap<>(MovementType.class);

        if (cashRegisters != null) {
            cashRegisters.stream()
                    .flatMap(cashRegisterMinDTO -> cashRegisterMinDTO.getCashMovements().stream())
                    //     .filter(cashMovementMinDTO -> cashMovementMinDTO.getCashMovementType() == CashMovementType.SALE) // Filtra apenas os movimentos de venda
                    .flatMap(cashMovementMinDTO -> cashMovementMinDTO.getMovementDetails().stream())
                    .forEach(detail -> {
                        MovementType type = detail.getMovementType();
                        BigDecimal amount = detail.getAmount();

                        // Adiciona o valor ao total acumulado ou inicializa se ainda não existe
                        totalizationCash.merge(type, amount, BigDecimal::add);
                    });
        }

        // Remove entradas com total igual a zero
        totalizationCash.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
        return totalizationCash;
    }

}
