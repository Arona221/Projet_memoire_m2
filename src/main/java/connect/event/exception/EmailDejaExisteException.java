package connect.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailDejaExisteException extends RuntimeException {
    public EmailDejaExisteException(String message) {
        super(message);
    }
}
