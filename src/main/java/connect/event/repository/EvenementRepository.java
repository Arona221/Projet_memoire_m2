package connect.event.repository;

import connect.event.entity.Evenement;
import connect.event.enums.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface EvenementRepository extends JpaRepository <Evenement, Long> {
    List<Evenement> findByCategorieAndDateAndLieu(Categorie categorie, Date date, String lieu);

    List<Evenement> findByCategorieAndDate(Categorie categorie, Date date);

    List<Evenement> findByCategorieAndLieu(Categorie categorie, String lieu);

    List<Evenement> findByDateAndLieu(Date date, String lieu);

    List<Evenement> findByCategorie(Categorie categorie);

    List<Evenement> findByDate(Date date);

    List<Evenement> findByLieu(String lieu);
}
