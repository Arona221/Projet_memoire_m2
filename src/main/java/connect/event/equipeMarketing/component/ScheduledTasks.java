package connect.event.equipeMarketing.component;

import connect.event.equipeMarketing.service.CampagneMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private CampagneMarketingService campagneService;

    @Scheduled(cron = "0 0 8 * * ?") // Tous les jours à 8h
    public void envoyerCampagnesProgrammees() {
        // Logique pour envoyer les campagnes programmées
        System.out.println("Vérification des campagnes programmées...");
    }
}
