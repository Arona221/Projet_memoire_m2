package connect.event.controller;

import connect.event.dto.ConnexionDTO;
import connect.event.dto.InscriptionDTO;
import connect.event.dto.TokenDTO;
import connect.event.dto.ValidationCompteDTO;
import connect.event.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import connect.event.dto.ConnexionResponseDTO;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/inscription")
    public ResponseEntity<TokenDTO> inscription(@Valid @RequestBody InscriptionDTO dto) {
        System.out.println("Tentative d'inscription reçue pour: " + dto.getEmail());
        return ResponseEntity.ok(authService.inscription(dto));
    }

    @PostMapping("/connexion")
    public ResponseEntity<ConnexionResponseDTO> connexion(@RequestBody ConnexionDTO dto) {
        // L'appel à la méthode de connexion du service AuthService pour récupérer le token
        TokenDTO token = authService.connexion(dto);

        // Création de la réponse avec un message et le token
        ConnexionResponseDTO response = new ConnexionResponseDTO("Utilisateur connecté", token);

        return ResponseEntity.ok(response); // Retourne la réponse avec le message et le token
    }

    @PostMapping("/valider")
    public ResponseEntity<TokenDTO> validerCompte(@Valid @RequestBody ValidationCompteDTO dto) {
        // L'appel à la méthode de validation du compte du service AuthService
        return ResponseEntity.ok(authService.validerCompte(dto));
    }
}
