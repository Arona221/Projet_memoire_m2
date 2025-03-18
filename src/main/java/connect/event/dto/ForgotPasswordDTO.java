package connect.event.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
}