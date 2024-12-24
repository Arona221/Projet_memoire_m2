package connect.event.service;

import connect.event.dto.ConnexionDTO;
import connect.event.dto.InscriptionDTO;
import connect.event.dto.TokenDTO;
import connect.event.dto.ValidationCompteDTO;

public interface AuthService {
    TokenDTO inscription(InscriptionDTO inscriptionDTO);
    TokenDTO connexion(ConnexionDTO connexionDTO);
    TokenDTO validerCompte(ValidationCompteDTO validationDTO);
}
