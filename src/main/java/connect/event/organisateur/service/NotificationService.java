package connect.event.organisateur.service;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import connect.event.organisateur.DTO.NotificationDTO;
import connect.event.organisateur.entity.Notification;
import connect.event.organisateur.enums.TypeNotification;
import connect.event.organisateur.repository.NotificationRepository;
import connect.event.participant.entity.SendNotification;
import connect.event.participant.repository.BilletAcheterRepository;
import connect.event.participant.repository.SendNotificationRepository;
import connect.event.participant.service.BilletAcheterService;
import connect.event.participant.service.FavorisService;
import connect.event.repository.EvenementRepository;
import connect.event.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private BilletAcheterService billetService;
    @Autowired
    private  FavorisService favorisService;
    @Autowired
    private  SendNotificationRepository sendNotificationRepository;

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
            notification.setEvenement(evenement);
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

    // Créer une notification
    public void createNotification(Utilisateur user, Evenement event, String type) {
        SendNotification notification = new SendNotification();
        notification.setUtilisateur(user);
        notification.setEvenement(event);
        notification.setTypeNotification(type);
        notification.setMessage(generateMessage(event, type));
        notification.setDateEnvoi(LocalDateTime.now());

        sendNotificationRepository.save(notification);
    }
    private String generateMessage(Evenement event, String type) {
        return type.equals("BILLET")
                ? "Rappel : Votre événement " + event.getNom() + " commence bientôt !"
                : "L'événement favori " + event.getNom() + " approche !";
    }

    // Planification des rappels
    @Scheduled(cron = "0 0 8 * * ?") // Tous les jours à 8h
    public void checkUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(2);

        // Pour les billets achetés
        List<Evenement> upcomingEvents = evenementRepository
                .findByDateBetween(now, future);

        upcomingEvents.forEach(event ->
                billetService.getParticipantsByEvent(event.getId_evenement())
                        .forEach(participant ->
                                createNotification(participant, event, "BILLET"))
        );

        // Pour les favoris
        upcomingEvents.forEach(event ->
                favorisService.getUsersByFavoriteEvent(event.getId_evenement())
                        .forEach(user ->
                                createNotification(user, event, "FAVORI"))
        );
    }

    // Purge automatique
    @Scheduled(cron = "0 0 0 * * ?") // Tous les jours à minuit
    @Transactional
    public void purgeOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(5);
        sendNotificationRepository.purgeOldNotifications(cutoff);
    }

    // Récupérer les notifications non lues
    public List<SendNotification> getUnreadNotifications(Long userId) {
        return sendNotificationRepository
                .findByUtilisateurIdUtilisateurAndReadFalse(userId);
    }

    // Marquer comme lu
    @Transactional
    public void markAsRead(Long notificationId) {
        sendNotificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    sendNotificationRepository.save(notification);
                });
    }

}