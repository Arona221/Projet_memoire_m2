package connect.event.participant.repository;

import connect.event.participant.entity.SendNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SendNotificationRepository extends JpaRepository<SendNotification, Long> {

    @Modifying
    @Query("DELETE FROM SendNotification sn WHERE sn.dateEnvoi < :cutoffDate")
    void purgeOldNotifications(LocalDateTime cutoffDate);
    // Modifier la méthode pour :
    List<SendNotification> findByUtilisateurIdUtilisateurAndReadFalse(Long utilisateurId);

}