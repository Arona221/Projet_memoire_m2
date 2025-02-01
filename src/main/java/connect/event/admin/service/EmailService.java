package connect.event.admin.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("adminEmailService")
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Envoie un email de notification à l'organisateur concernant le changement de statut d'un événement.
     */
    public void envoyerNotificationStatut(String email, String prenom, String nom, String nomEvenement, String statut) {
        String sujet = "Mise à jour du statut de votre événement : " + nomEvenement;
        String corps = """
                Bonjour %s %s,

                Le statut de votre événement "%s" a été mis à jour.

                🆕 Statut actuel : %s.

                Merci de consulter votre espace organisateur pour plus de détails.

                Cordialement,
                L'équipe ConnectEvent.
                """.formatted(prenom, nom, nomEvenement, statut);

        envoyerEmail(email, sujet, corps);
    }

    /**
     * Envoie un email générique.
     */
    public void envoyerEmail(String to, String subject, String text) {
        try {
            if (to == null || to.trim().isEmpty()) {
                throw new IllegalArgumentException("L'adresse email du destinataire est invalide !");
            }

            // Suppression des espaces inutiles
            to = to.trim();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom("arona10ndiaye@gmail.com"); // Ton email vérifié

            mailSender.send(message);
            logger.info("Email envoyé à {}", to);
        } catch (IllegalArgumentException e) {
            logger.error("Adresse email invalide : {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email à {}", to, e);
        }
    }

}
