package connect.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UtilisateurNonTrouveException extends RuntimeException {
    public UtilisateurNonTrouveException(String message) {
        super(message);
    }
}
