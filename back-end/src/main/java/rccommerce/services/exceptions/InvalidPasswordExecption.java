package rccommerce.services.exceptions;

import java.io.Serial;

public class InvalidPasswordExecption extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidPasswordExecption(String msg) {
        super(msg);
    }

}
