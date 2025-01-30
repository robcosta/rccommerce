package rccommerce.services.exceptions;

import java.io.Serial;

public class MessageToUsersException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MessageToUsersException(String msg) {
        super(msg);
    }

}
