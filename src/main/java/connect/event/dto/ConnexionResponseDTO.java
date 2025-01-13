package connect.event.dto;

import lombok.Data;

@Data
public class ConnexionResponseDTO {
    private String message;
    private TokenDTO token;

    public ConnexionResponseDTO(String message, TokenDTO token) {
        this.message = message;
        this.token = token;
    }
}
