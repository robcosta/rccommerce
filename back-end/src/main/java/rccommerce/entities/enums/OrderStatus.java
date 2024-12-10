package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum OrderStatus {

    WAITING_PAYMENT(0, "Aguardando Pagamento"),
    PAID(1, "Pago"),
    SHIPPED(2, "Enviado"),
    DELIVERED(3, "Entregue"),
    CANCELED(4, "Cancelado");

    private final Integer code;
    private final String description;

    private OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<OrderStatus> searchCode(Integer code) {
        return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
    }

    public static Optional<OrderStatus> searchDescription(String description) {
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
    public static OrderStatus fromValue(String value) {
        for (OrderStatus very : OrderStatus.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Status: '" + value + "' inexistente.");
    }
}
