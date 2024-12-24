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


@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<TokenDTO> connexion(@Valid @RequestBody ConnexionDTO dto) {
        // L'appel à la méthode de connexion du service AuthService
        return ResponseEntity.ok(authService.connexion(dto));
    }

    @PostMapping("/valider")
    public ResponseEntity<TokenDTO> validerCompte(@Valid @RequestBody ValidationCompteDTO dto) {
        // L'appel à la méthode de validation du compte du service AuthService
        return ResponseEntity.ok(authService.validerCompte(dto));
    }
}
