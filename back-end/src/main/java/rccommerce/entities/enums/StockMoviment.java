package rccommerce.entities.enums;

import java.util.Arrays;
import java.util.Optional;

public enum StockMoviment {
	
	BUY(1),
	INPUT(2),
	SALE(3),
	OUTPUT(4),
	TRANSFER(5);
	
	private final Integer code;
	
	private StockMoviment(int code) {
		this.code = code;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public static Optional<StockMoviment> searchCode(Integer code) {
		return Arrays.stream(values()).sequential().filter(t -> t.code.equals(code)).findFirst();
	}
}
