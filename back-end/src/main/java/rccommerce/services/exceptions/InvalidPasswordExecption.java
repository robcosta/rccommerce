package rccommerce.services.exceptions;

public class InvalidPasswordExecption extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InvalidPasswordExecption(String msg) {
		super(msg);
	}

}
