package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum OrderStatus {

    WAITING_PAYMENT(0),
    PAID(1),
    SHIPPED(2),
    DELIVERED(3),
    CANCELED(4);

    private final Integer code;

    private OrderStatus(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Optional<OrderStatus> searchCode(Integer code) {
        return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        for (OrderStatus very : OrderStatus.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Status: '" + value + "' inexistente.");
    }
}
