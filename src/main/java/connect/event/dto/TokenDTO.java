package connect.event.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private String token;
    private String type = "Bearer";

    public TokenDTO(String token) {
        this.token = token;
    }
}
