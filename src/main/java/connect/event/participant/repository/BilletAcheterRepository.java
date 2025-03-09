package connect.event.participant.repository;

import connect.event.entity.Utilisateur;
import connect.event.participant.entity.BilletAcheter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BilletAcheterRepository extends JpaRepository<BilletAcheter, Long> {
    List<BilletAcheter> findByReferenceTransaction(String referenceTransaction);

    @Query("SELECT b.participant FROM BilletAcheter b WHERE b.evenement.id_evenement = :evenementId")
    List<Utilisateur> findParticipantsByEvenement(Long evenementId);

    @Query("SELECT ba FROM BilletAcheter ba JOIN FETCH ba.evenement JOIN FETCH ba.billet WHERE ba.participant.idUtilisateur = :participantId")
    List<BilletAcheter> findByParticipantIdWithDetails(Long participantId);

    @Query("SELECT new map("
            + "COUNT(DISTINCT ba.participant.idUtilisateur) as participantCount, "
            + "COALESCE(SUM(ba.quantite), 0L) as totalTickets) " // Ajout de COALESCE
            + "FROM BilletAcheter ba "
            + "WHERE ba.evenement.id_evenement = :eventId")
    Map<String, Object> getEventStats(@Param("eventId") Long eventId);

    @Query("SELECT ba FROM BilletAcheter ba WHERE ba.evenement.id_evenement = :evenementId")
    List<BilletAcheter> findByEvenement_Id_evenement(@Param("evenementId") Long evenementId);


}
