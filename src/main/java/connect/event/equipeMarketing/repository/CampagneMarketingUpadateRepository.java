package connect.event.equipeMarketing.repository;

import connect.event.equipeMarketing.emuns.StatutCampagne;
import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CampagneMarketingUpadateRepository extends JpaRepository<CampagneMarketingUpdate, Long> {

    @Query("SELECT c FROM CampagneMarketingUpdate c WHERE " +
            "c.datePublicationPlanifiee <= :date AND " +
            "c.heurePublicationPlanifiee <= :heure AND " +
            "c.statut = :statut")
    List<CampagneMarketingUpdate> findCampagnesAPublier(
            @Param("date") LocalDate date,
            @Param("heure") LocalTime heure,
            @Param("statut") StatutCampagne statut);

    // ✅ Récupérer les campagnes planifiées
    List<CampagneMarketingUpdate> findByStatut(StatutCampagne statut);

}
