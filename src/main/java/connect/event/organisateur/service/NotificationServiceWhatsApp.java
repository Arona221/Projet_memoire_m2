package connect.event.organisateur.service;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import connect.event.organisateur.DTO.NotificationDTO;
import connect.event.organisateur.entity.Notification;
import connect.event.organisateur.enums.TypeNotification;
import connect.event.organisateur.repository.NotificationRepository;
import connect.event.participant.repository.BilletAcheterRepository;
import connect.event.repository.EvenementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationServiceWhatsApp {

    private final NotificationRepository notificationRepository;
    private final BilletAcheterRepository billetAcheterRepository;
    private final EvenementRepository evenementRepository;
    private final TwilioWhatsAppService twilioWhatsAppService;

    public NotificationServiceWhatsApp(NotificationRepository notificationRepository,
                                       BilletAcheterRepository billetAcheterRepository,
                                       EvenementRepository evenementRepository,
                                       TwilioWhatsAppService twilioWhatsAppService) {
        this.notificationRepository = notificationRepository;
        this.billetAcheterRepository = billetAcheterRepository;
        this.evenementRepository = evenementRepository;
        this.twilioWhatsAppService = twilioWhatsAppService;
    }

    public void envoyerNotificationWhatsApp(Long organisateurId, Long evenementId, NotificationDTO notificationDTO) {
        // Vérifier que l'événement existe
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));

        // Vérifier que l'utilisateur est bien l'organisateur
        if (!evenement.getOrganisateur().getIdUtilisateur().equals(organisateurId)) {
            throw new RuntimeException("Vous n'êtes pas l'organisateur de cet événement");
        }

        // Récupérer tous les participants de l'événement
        List<Utilisateur> participants = billetAcheterRepository.findParticipantsByEvenement(evenementId);
        if (participants.isEmpty()) {
            throw new RuntimeException("Aucun participant trouvé pour cet événement");
        }

        // Envoyer la notification WhatsApp à chaque participant
        for (Utilisateur participant : participants) {
            // Enregistrer la notification dans la base de données
            Notification notification = new Notification();
            notification.setDate(LocalDate.now());
            notification.setContenu(notificationDTO.getContenu());
            notification.setTypeNotification(TypeNotification.valueOf(notificationDTO.getTypeNotification()));
            notification.setParticipant(participant);
            notificationRepository.save(notification);

            // Envoyer le message WhatsApp
            if (participant.getPhoneNumber() != null && !participant.getPhoneNumber().isEmpty()) {
                twilioWhatsAppService.sendWhatsAppMessage(participant.getPhoneNumber(), notificationDTO.getContenu());
            } else {
                System.out.println("⚠️ Numéro de téléphone manquant pour : " + participant.getEmail());
            }
        }
    }
}
