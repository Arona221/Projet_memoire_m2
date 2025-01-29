package connect.event.participant.repository;

import connect.event.participant.entity.Favoris;
import connect.event.entity.Utilisateur;
import connect.event.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FavorisRepository extends JpaRepository<Favoris, Long> {

    // Trouver un favoris par utilisateur et événement
    Optional<Favoris> findByUtilisateurAndEvenement(Utilisateur utilisateur, Evenement evenement);

    // Supprimer un favoris par utilisateur et événement
    @Modifying
    @Transactional
    @Query("DELETE FROM Favoris f WHERE f.utilisateur.id = :idUtilisateur AND f.evenement.id = :idEvenement")
    void deleteByUtilisateurIdAndEvenementId(@Param("idUtilisateur") Long idUtilisateur, @Param("idEvenement") Long idEvenement);
}
