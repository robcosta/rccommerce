package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum PaymentType {

    MONEY(1),
    PIX(2),
    CREDIT_CARD(3),
    DEBIT_CARD(4),
    FOOD_VOUCHER(5),
    CANCELED(6);

    private final Integer code;

    private PaymentType(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Optional<PaymentType> searchCode(Integer code) {
        return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static PaymentType fromValue(String value) {
        for (PaymentType very : PaymentType.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Pagamento do tipo: '" + value + "' inexistente.");
    }
}
