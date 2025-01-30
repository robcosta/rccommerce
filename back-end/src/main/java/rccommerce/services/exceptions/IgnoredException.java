package rccommerce.services.exceptions;

import java.io.Serial;

public class IgnoredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public IgnoredException(String msg) {
        super(msg);
    }

}
