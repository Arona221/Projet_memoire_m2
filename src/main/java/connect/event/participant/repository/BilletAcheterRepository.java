package connect.event.participant.repository;

import connect.event.entity.Utilisateur;
import connect.event.participant.entity.BilletAcheter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BilletAcheterRepository extends JpaRepository<BilletAcheter, Long> {
    Optional<BilletAcheter> findByReferenceTransaction(String referenceTransaction);
    @Query("SELECT b.participant FROM BilletAcheter b WHERE b.evenement.id_evenement = :evenementId")
    List<Utilisateur> findParticipantsByEvenement(Long evenementId);
}
