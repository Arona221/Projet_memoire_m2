package connect.event.service;

import connect.event.dto.*;

public interface AuthService {
    TokenDTO inscription(InscriptionDTO inscriptionDTO);
    TokenDTO connexion(ConnexionDTO connexionDTO);
    TokenDTO validerCompte(ValidationCompteDTO validationDTO);

    void initiatePasswordReset(ForgotPasswordDTO dto);

    void resetPassword(ResetPasswordDTO dto);
}
