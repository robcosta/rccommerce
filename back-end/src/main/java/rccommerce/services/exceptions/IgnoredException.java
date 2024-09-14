package rccommerce.services.exceptions;

public class IgnoredException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public IgnoredException(String msg) {
		super(msg);
	}

}
