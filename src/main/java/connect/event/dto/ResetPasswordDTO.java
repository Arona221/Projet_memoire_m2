package connect.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    private String newPassword;
}