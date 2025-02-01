package connect.event.service;

import connect.event.dto.ConnexionDTO;
import connect.event.dto.InscriptionDTO;
import connect.event.dto.TokenDTO;
import connect.event.dto.ValidationCompteDTO;
import connect.event.entity.Utilisateur;
import connect.event.exception.*;
import connect.event.repository.UtilisateurRepository;
import connect.event.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public TokenDTO inscription(InscriptionDTO dto) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDejaExisteException("Cet email est déjà utilisé");
        }
        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setNom(dto.getNom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setTypeUtilisateur(dto.getTypeUtilisateur());
        // Générer le code de validation
        String code = genererCode();
        utilisateur.setCodeValidation(code);
        utilisateur.setCodeValidationExpiration(LocalDateTime.now().plusHours(24));
        // Sauvegarder l'utilisateur
        utilisateurRepository.save(utilisateur);

        // Envoyer l'email
        emailService.envoyerCodeValidation(utilisateur.getEmail(), code);

        return new TokenDTO(jwtUtil.generateToken(String.valueOf(utilisateur)));
    }
    @Override
    public TokenDTO connexion(ConnexionDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UtilisateurNonTrouveException("Email ou mot de passe incorrect"));

        if (!utilisateur.getActive()) {
            throw new CompteNonActiveException("Compte non activé");
        }

        if (!passwordEncoder.matches(dto.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new CredentialsInvalidesException("Email ou mot de passe incorrect");
        }

        return new TokenDTO(jwtUtil.generateToken(String.valueOf(utilisateur)));
    }
    @Override
    public TokenDTO validerCompte(ValidationCompteDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur non trouvé"));

        if (utilisateur.getActive()) {
            throw new CompteDejaActiveException("Ce compte est déjà activé");
        }

        if (utilisateur.getCodeValidationExpiration().isBefore(LocalDateTime.now())) {
            throw new CodeValidationExpireException("Le code de validation a expiré");
        }

        if (!utilisateur.getCodeValidation().equals(dto.getCode())) {
            throw new CodeValidationInvalideException("Code de validation invalide");
        }

        utilisateur.setActive(true);
        utilisateur.setCodeValidation(null);
        utilisateur.setCodeValidationExpiration(null);
        utilisateurRepository.save(utilisateur);

        return new TokenDTO(jwtUtil.generateToken(String.valueOf(utilisateur)));
    }

    private String genererCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}