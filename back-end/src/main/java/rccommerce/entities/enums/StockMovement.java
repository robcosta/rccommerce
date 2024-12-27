package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum StockMovement {

    BUY(1, "Compra"),
    SALE(2, "Venda"),
    INPUT(3, "Entrada"),
    OUTPUT(4, "Saída"),
    TRANSFER(5, "Transferência");

    private final Integer code;
    private final String description;

    private StockMovement(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<StockMovement> searchCode(Integer code) {
        return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
    }

    public static Optional<StockMovement> searchDescription(String description) {
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
    public static StockMovement fromValue(String value) {
        for (StockMovement type : StockMovement.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidArgumentExecption("Movimento de caixa do tipo: '" + value + "' inexistente.");
    }
}
