package rccommerce.services.exceptions;

public class InvalidArgumentExecption extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InvalidArgumentExecption(String msg) {
		super(msg);
	}

}
