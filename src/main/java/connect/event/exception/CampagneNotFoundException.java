package connect.event.exception;

public class CampagneNotFoundException extends RuntimeException {
    public CampagneNotFoundException(String message) {
        super(message);
    }
}