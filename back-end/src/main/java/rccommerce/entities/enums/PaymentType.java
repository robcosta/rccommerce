package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentType {
	
	MONEY(1),
	PIX(2),
	CREDIT_CARD(3),
	DEBIT_CARD(4),
	FOOD_VOUCHER(5);
	
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
}
