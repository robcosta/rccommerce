package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum CashMovementType {

    // Entradas
    SALE(1, "Venda"),
    REINFORCEMENT(2, "Reforço de Caixa"),
    DIVERSE_RECEIPT(3, "Recebimento Diverso"),
    INTEREST_OR_FINE(4, "Juros ou Multas"),
    OTHER_RECEIPTS(5, "Outros Recebimentos"),
    // Saídas
    WITHDRAWAL(6, "Retirada de Caixa"),
    INITIAL_CHANGE(7, "Troco Inicial"),
    OPERATIONAL_EXPENSE(8, "Despesa Operacional"),
    REIMBURSEMENT(9, "Reembolso"),
    DISCOUNT(10, "Desconto"),
    OTHER_EXPENSES(11, "Outras Saídas");

    private final Integer code;
    private final String description;

    private CashMovementType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<CashMovementType> searchCode(Integer code) {
        return Arrays.stream(values())
                .sequential()
                .filter(t -> t.code.equals(code))
                .findFirst();
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static CashMovementType fromValue(String value) {
        for (CashMovementType type : CashMovementType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidArgumentExecption("Movimento de caixa do tipo: '" + value + "' inexistente.");
    }
}
