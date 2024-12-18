package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum MovimentType {

    MONEY(1, "Dinheiro"),
    PIX(2, "PIX"),
    CREDIT_CARD(3, "Cartão de Crédito"),
    DEBIT_CARD(4, "Cartão de Débito"),
    FOOD_VOUCHER(5, "Vale Refeição"),
    OTHER(6, "Outro"),
    CANCELED(7, "Cancelado");

    private final Integer code;
    private final String description;

    private MovimentType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<MovimentType> searchCode(Integer code) {
        return Arrays.stream(values())
                .sequential()
                .filter(t -> t.code.equals(code))
                .findFirst();
    }

    public static Optional<MovimentType> searchDescription(String description) {
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
    public static MovimentType fromValue(String value) {
        for (MovimentType very : MovimentType.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Pagamento do tipo: '" + value + "' inexistente.");
    }
}
