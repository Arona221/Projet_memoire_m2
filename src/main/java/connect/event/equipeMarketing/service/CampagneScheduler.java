package connect.event.equipeMarketing.service;

import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import connect.event.equipeMarketing.emuns.StatutCampagne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

import static connect.event.service.EvenementService.logger;

@EnableScheduling
@Component
public class CampagneScheduler {

    @Autowired
    private CampagneMarketingServiceUpdate campagneService;

    @Scheduled(cron = "0 * * * * *")
    public void publierCampagnesPlanifiees() {
        logger.info("🔎 Vérification des campagnes planifiées...");

        List<CampagneMarketingUpdate> campagnes = campagneService.getCampagnesAPublier();
        logger.info("📋 Campagnes à publier: {}", campagnes.size());

        campagnes.forEach(campagne -> {
            try {
                logger.info("🚀 Publication campagne ID {} - {} prévue le {} à {}",
                        campagne.getId(), campagne.getNom(),
                        campagne.getDatePublicationPlanifiee(),
                        campagne.getHeurePublicationPlanifiee());

                campagneService.publierCampagne(campagne.getId());
                campagne.setStatut(StatutCampagne.En_cours);

                logger.info("✅ Succès publication ID {}", campagne.getId());
            } catch (Exception e) {
                logger.error("💥 Échec publication ID {}: {}", campagne.getId(), e.getMessage());
                campagne.setStatut(StatutCampagne.ERREUR);
            }
            campagneService.save(campagne);
        });
    }
}
