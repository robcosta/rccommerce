package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

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
}
