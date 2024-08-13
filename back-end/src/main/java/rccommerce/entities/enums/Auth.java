package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Auth {

	ALL(0), CREATE(1), READER(2), UPDATE(3), DELETE(4), NONE(99);

	private final Integer code;

	Auth(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static Optional<Auth> searchCode(Integer code) {
		return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
	}
}
