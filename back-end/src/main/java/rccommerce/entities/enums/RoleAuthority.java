package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum RoleAuthority {

    ROLE_ADMIN(1),
    ROLE_OPERATOR(2),
    ROLE_SELLER(3),
    ROLE_CLIENT(4),
    ROLE_CASH(5);

    private final Integer code;

    RoleAuthority(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Optional<RoleAuthority> searchCode(Integer code) {
        return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static RoleAuthority fromValue(String value) {
        for (RoleAuthority very : RoleAuthority.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Um ou mais valores no campo 'Role' não são válidos: " + value);
    }
}
