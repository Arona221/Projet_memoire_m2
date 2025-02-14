package connect.event.repository;

import connect.event.entity.Evenement;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface EvenementRepository extends JpaRepository <Evenement, Long>  {
    List<Evenement> findByCategorieAndDateAndLieu(Categorie categorie, Date date, String lieu);

    List<Evenement> findByCategorieAndDate(Categorie categorie, Date date);

    List<Evenement> findByCategorieAndLieu(Categorie categorie, String lieu);

    List<Evenement> findByDateAndLieu(Date date, String lieu);

    List<Evenement> findByCategorie(Categorie categorie);

    List<Evenement> findByDate(Date date);

    List<Evenement> findByLieu(String lieu);

    // Pagination de base
    Page<Evenement> findByOrganisateur_IdUtilisateur(Long organisateurId, Pageable pageable);

    // Recherche par nom
    Page<Evenement> findByOrganisateur_IdUtilisateurAndNomContainingIgnoreCase(
            Long organisateurId,
            String nom,
            Pageable pageable
    );

    // Recherche par statut
    Page<Evenement> findByOrganisateur_IdUtilisateurAndStatus(
            Long organisateurId,
            Status status,
            Pageable pageable
    );

    // Recherche combinée nom + statut
    Page<Evenement> findByOrganisateur_IdUtilisateurAndStatusAndNomContainingIgnoreCase(
            Long organisateurId,
            Status status,
            String nom,
            Pageable pageable
    );

}
