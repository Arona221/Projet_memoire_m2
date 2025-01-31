package connect.event.exception;

public class PayDunyaException extends RuntimeException {
    public PayDunyaException(String message) {
        super(message);
    }

    public PayDunyaException(String message, Throwable cause) {
        super(message, cause);
    }
}

