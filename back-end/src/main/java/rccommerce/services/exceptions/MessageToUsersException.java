package rccommerce.services.exceptions;

public class MessageToUsersException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MessageToUsersException(String msg) {
        super(msg);
    }

}
