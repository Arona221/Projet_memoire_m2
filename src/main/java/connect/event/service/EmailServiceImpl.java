package connect.event.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("UtlisateurEmailService")
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void envoyerCodeValidation(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Code de Validation");
        message.setText("Votre code de validation est : " + code);
        javaMailSender.send(message);
    }
    @Override
    public void envoyerNotificationStatut(String to, String prenom, String nom, String nomEvenement, String statut) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Mise à jour du statut de votre événement");
        message.setText("Bonjour " + prenom + " " + nom + ",\n\n"
                + "Votre événement \"" + nomEvenement + "\" a été " + statut + ".\n\n"
                + "Cordialement,\nL'équipe de ConnectEvent.");

        javaMailSender.send(message);
    }
}
