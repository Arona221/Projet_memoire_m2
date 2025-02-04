package connect.event.organisateur.service;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import connect.event.organisateur.DTO.NotificationDTO;
import connect.event.organisateur.entity.Notification;
import connect.event.organisateur.enums.TypeNotification;
import connect.event.organisateur.repository.NotificationRepository;
import connect.event.participant.repository.BilletAcheterRepository;
import connect.event.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BilletAcheterRepository billetAcheterRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private JavaMailSender1 mailSender1;
    @Autowired
    private OrangeApiService orangeApiService;

    @Autowired
    private SmsService smsService;

    public void envoyerNotification(Long organisateurId, Long evenementId, NotificationDTO notificationDTO) throws MessagingException {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));

        if (!evenement.getOrganisateur().getIdUtilisateur().equals(organisateurId)) {
            throw new RuntimeException("Vous n'êtes pas l'organisateur de cet événement");
        }

        List<Utilisateur> participants = billetAcheterRepository.findParticipantsByEvenement(evenementId);

        if (participants.isEmpty()) {
            throw new RuntimeException("Aucun participant trouvé pour cet événement");
        }

        for (Utilisateur participant : participants) {
            Notification notification = new Notification();
            notification.setDate(LocalDate.now());
            notification.setContenu(notificationDTO.getContenu());
            notification.setTypeNotification(TypeNotification.valueOf(notificationDTO.getTypeNotification()));
            notification.setParticipant(participant);
            notificationRepository.save(notification);

            envoyerEmail(participant.getEmail(), "Notification pour votre événement", notificationDTO.getContenu());
        }
    }

    private void envoyerEmail(String destinataire, String sujet, String message) throws MessagingException {
        MimeMessage mimeMessage = mailSender1.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(destinataire);
        helper.setSubject(sujet);
        helper.setText(message, true);
        mailSender1.send(mimeMessage);
    }

}