package rccommerce.entities.enums;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum CashMovementType {

    // Entradas
    SALE(1, "Venda", BigDecimal.ONE),
    REINFORCEMENT(2, "Reforço de Caixa", BigDecimal.ONE),
    DIVERSE_RECEIPT(3, "Recebimento Diverso", BigDecimal.ONE),
    INTEREST_OR_FINE(4, "Juros ou Multas", BigDecimal.ONE),
    OTHER_RECEIPTS(5, "Outros Recebimentos", BigDecimal.ONE),
    // Entrada - Abertura
    OPENING_BALANCE(12, "Abertura de caixa", BigDecimal.ONE),
    // Saídas
    WITHDRAWAL(6, "Retirada de Caixa", BigDecimal.valueOf(-1)),
    INITIAL_CHANGE(7, "Troco Inicial", BigDecimal.valueOf(-1)),
    OPERATIONAL_EXPENSE(8, "Despesa Operacional", BigDecimal.valueOf(-1)),
    REIMBURSEMENT(9, "Reembolso", BigDecimal.valueOf(-1)),
    DISCOUNT(10, "Desconto", BigDecimal.valueOf(-1)),
    OTHER_EXPENSES(11, "Outras Saídas", BigDecimal.valueOf(-1)),
    // Saída - Fechamento
    CLOSING_BALANCE(13, "Fechamento de caixa", BigDecimal.valueOf(-1));

    private final Integer code;
    private final String description;
    private final BigDecimal factor;

    private CashMovementType(int code, String description, BigDecimal factor) {
        this.code = code;
        this.description = description;
        this.factor = factor;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    // Retorna o amount como positivo(entrada) ou negativo(saída)
    public BigDecimal applyFactor(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("O valor não pode ser nulo.");
        }
        return factor.signum() < 0 ? amount.negate() : amount;
    }

    public static Optional<CashMovementType> searchCode(Integer code) {
        return Arrays.stream(values())
                .sequential()
                .filter(t -> t.code.equals(code))
                .findFirst();
    }

    public static Optional<CashMovementType> searchDescription(String description) {
        return Arrays.stream(values())
                .sequential()
                .filter(t -> t.description.equals(description))
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
