package rccommerce.services.exceptions;

import java.io.Serial;

public class InvalidArgumentExecption extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidArgumentExecption(String msg) {
        super(msg);
    }

}
