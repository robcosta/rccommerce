package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum PermissionAuthority {

    PERMISSION_ALL(1),
    PERMISSION_NONE(2),
    PERMISSION_CREATE(3),
    PERMISSION_READER(4),
    PERMISSION_UPDATE(5),
    PERMISSION_DELETE(6),
    PERMISSION_ROLE(7),
    PERMISSION_PERMISSION(8);

    private final Integer code;

    PermissionAuthority(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Optional<PermissionAuthority> searchCode(Integer code) {
        return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static PermissionAuthority fromValue(String value) {
        for (PermissionAuthority very : PermissionAuthority.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Um ou mais valores no campo 'permission' não são válidos: " + value);
    }
}
