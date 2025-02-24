package connect.event.repository;

import connect.event.entity.Notificationpart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationpartRepository extends JpaRepository<Notificationpart, Long> {
    @Query("SELECT n FROM Notificationpart n WHERE n.utilisateur.id = :utilisateurId AND n.deleted = false ORDER BY n.dateEnvoi DESC")
    List<Notificationpart> findNotificationsForUser(@Param("utilisateurId") Long utilisateurId);
    @Modifying
    @Query("UPDATE Notificationpart n SET n.deleted = true WHERE n.utilisateur.idUtilisateur = :userId AND n.deleted = false")
    void markAllAsRead(@Param("userId") Long userId);

    @Query("SELECT n FROM Notificationpart n WHERE n.utilisateur.idUtilisateur = :userId AND n.deleted = false ORDER BY n.dateEnvoi DESC")
    List<Notificationpart> findByUtilisateurIdAndDeletedFalse(@Param("userId") Long userId);
}

