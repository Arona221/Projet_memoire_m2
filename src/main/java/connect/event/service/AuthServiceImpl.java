package connect.event.service;

import connect.event.dto.*;
import connect.event.entity.Utilisateur;
import connect.event.exception.*;
import connect.event.repository.UtilisateurRepository;
import connect.event.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    /**
     * Inscription d'un nouvel utilisateur.
     */
    @Override
    public TokenDTO inscription(InscriptionDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDejaExisteException("Cet email est déjà utilisé");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setNom(dto.getNom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setPhoneNumber(dto.getPhoneNumber());
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setTypeUtilisateur(dto.getTypeUtilisateur());

        // Génération du code de validation
        String code = genererCode();
        utilisateur.setCodeValidation(code);
        utilisateur.setCodeValidationExpiration(LocalDateTime.now().plusHours(24));

        utilisateurRepository.save(utilisateur);

        emailService.envoyerCodeValidation(utilisateur.getEmail(), code);

        // Passer une liste vide ou des rôles, selon le besoin
        List<String> roles = new ArrayList<>(); // Liste vide, mais vous pouvez ajouter des rôles si nécessaire
        String token = jwtUtil.generateToken(utilisateur.getEmail(), roles);

        return new TokenDTO(token, utilisateur.getTypeUtilisateur().name(), utilisateur.getNom(), utilisateur.getEmail(), utilisateur.getIdUtilisateur());
    }

    /**
     * Connexion d'un utilisateur existant.
     */
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

        // Passer une liste vide ou des rôles, selon le besoin
        List<String> roles = new ArrayList<>(); // Liste vide, mais vous pouvez ajouter des rôles si nécessaire
        String token = jwtUtil.generateToken(utilisateur.getEmail(), roles);

        return new TokenDTO(token, utilisateur.getTypeUtilisateur().name(), utilisateur.getNom(), utilisateur.getEmail(), utilisateur.getIdUtilisateur());
    }

    /**
     * Validation du compte via le code envoyé par email.
     */
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

        // Passer une liste vide ou des rôles, selon le besoin
        List<String> roles = new ArrayList<>(); // Liste vide, mais vous pouvez ajouter des rôles si nécessaire
        String token = jwtUtil.generateToken(utilisateur.getEmail(), roles);

        return new TokenDTO(token, utilisateur.getTypeUtilisateur().name(), utilisateur.getNom(), utilisateur.getEmail(), utilisateur.getIdUtilisateur());
    }

    /**
     * Génération d'un code de validation aléatoire à 6 chiffres.
     */
    private String genererCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public void initiatePasswordReset(ForgotPasswordDTO dto) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(dto.getEmail());
        if (userOptional.isEmpty()) {
            return; // Ne pas révéler que l'email n'existe pas
        }
        Utilisateur user = userOptional.get();
        String code = genererCode();
        user.setResetPasswordCode(code);
        user.setResetPasswordCodeExpiration(LocalDateTime.now().plusHours(1));
        utilisateurRepository.save(user);
        emailService.envoyerResetPasswordEmail(user.getEmail(), code);
    }
    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        Utilisateur user = utilisateurRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur non trouvé"));

        if (user.getResetPasswordCode() == null || user.getResetPasswordCodeExpiration() == null) {
            throw new CodeValidationInvalideException("Aucun code de réinitialisation trouvé");
        }

        if (user.getResetPasswordCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new CodeValidationExpireException("Le code de réinitialisation a expiré");
        }

        if (!user.getResetPasswordCode().equals(dto.getCode())) {
            throw new CodeValidationInvalideException("Code de réinitialisation invalide");
        }

        user.setMotDePasse(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetPasswordCode(null);
        user.setResetPasswordCodeExpiration(null);
        utilisateurRepository.save(user);
    }
}
