package connect.event.repository;

import connect.event.participant.entity.SalesMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SalesMetricsRepository extends JpaRepository<SalesMetrics, Long> {

    @Query("SELECT sm FROM SalesMetrics sm WHERE sm.eventId = :eventId")
    Optional<SalesMetrics> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT sm FROM SalesMetrics sm WHERE sm.organisateurId = :organisateurId")
    List<SalesMetrics> findByOrganisateurId(@Param("organisateurId") Long organisateurId);

}