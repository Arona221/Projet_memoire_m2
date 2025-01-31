package connect.event.participant.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendBilletEmail(String toEmail, String participantName, String evenementName, int quantite, BigDecimal montantTotal, String referenceTransaction, byte[] qrCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Confirmation d'achat de billet");

        String emailText = String.format(
                "Bonjour %s,\n\n" +
                        "Votre paiement a été validé avec succès.\n" +
                        "Voici votre billet pour l'événement %s.\n\n" +
                        "Détails :\n" +
                        "- Quantité : %d\n" +
                        "- Montant : %.2f XOF\n" +
                        "- Référence : %s\n\n" +
                        "Veuillez trouver ci-joint votre billet avec un QR Code.\n\n" +
                        "Merci pour votre achat !",
                participantName, evenementName, quantite, montantTotal, referenceTransaction
        );

        helper.setText(emailText);
        helper.addAttachment("billet_qrcode.png", () -> new ByteArrayInputStream(qrCode));

        javaMailSender.send(message);
    }
}