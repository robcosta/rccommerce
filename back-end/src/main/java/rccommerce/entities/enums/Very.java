package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public enum Very {

	ALL(1), CREATE(2), READER(3), UPDATE(4), DELETE(5), NONE(6);

	private final Integer code;

	Very(int code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public static Optional<Very> searchCode(Integer code) {
		return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
	}
	
	@JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static Very fromValue(String value) {
        for (Very very : Very.values()) {
            if (very.name().equalsIgnoreCase(value)) {
                return very;
            }
        }
        throw new InvalidArgumentExecption("Um ou mais valores no campo 'very' não são válidos: " + value);
    }
}
