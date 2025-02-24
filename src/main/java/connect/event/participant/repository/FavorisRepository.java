package connect.event.participant.repository;

import connect.event.participant.entity.Favoris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavorisRepository extends JpaRepository<Favoris, Long> {

    // Remplacer la méthode dérivée par une requête nommée
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favoris f WHERE f.utilisateur.idUtilisateur = ?1 AND f.evenement.id_evenement = ?2")
    boolean existsByUtilisateurIdAndEvenementId(Long utilisateurId, Long evenementId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favoris f WHERE f.utilisateur.idUtilisateur = ?1 AND f.evenement.id_evenement = ?2")
    void deleteByUtilisateurIdAndEvenementId(Long utilisateurId, Long evenementId);

    Page<Favoris> findByUtilisateurIdUtilisateur(Long utilisateurId, Pageable pageable);
}