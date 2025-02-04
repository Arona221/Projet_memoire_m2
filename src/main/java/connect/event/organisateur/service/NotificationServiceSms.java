package connect.event.organisateur.service;

import connect.event.entity.Utilisateur;
import connect.event.participant.repository.BilletAcheterRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationServiceSms {

    private final OrangeApiService orangeApiService;
    private final SmsService smsService;
    private final BilletAcheterRepository billetAcheterRepository;

    public NotificationServiceSms(OrangeApiService orangeApiService, SmsService smsService, BilletAcheterRepository billetAcheterRepository) {
        this.orangeApiService = orangeApiService;
        this.smsService = smsService;
        this.billetAcheterRepository = billetAcheterRepository;
    }

    public void envoyerNotificationParSms(Long organisateurId, Long evenementId, String contenu) throws MessagingException {
        try {
            String accessToken = orangeApiService.getAccessToken();
            List<Utilisateur> participants = billetAcheterRepository.findParticipantsByEvenement(evenementId);

            for (Utilisateur participant : participants) {
                if (participant.getPhoneNumber() != null && !participant.getPhoneNumber().isEmpty()) {
                    smsService.sendSms(accessToken, participant.getPhoneNumber(), contenu);
                } else {
                    System.out.println("⚠️ Numéro de téléphone manquant pour l'utilisateur : " + participant.getEmail());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi des notifications SMS: " + e.getMessage());
        }
    }
}
