package connect.event.specifications;

import connect.event.entity.Evenement;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EvenementSpecifications {

    public static Specification<Evenement> createSpecification(
            String search,
            String categorie,
            Date date,
            String lieu,
            Status status) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ajouter toujours la condition de status APPROUVE
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Recherche par nom
            if (search != null && !search.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nom")),
                        "%" + search.toLowerCase() + "%"
                ));
            }

            // Filtre par catégorie
            if (categorie != null && !categorie.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("categorie"),
                        Categorie.valueOf(categorie.toUpperCase())
                ));
            }

            // Filtre par date
            if (date != null) {
                predicates.add(criteriaBuilder.equal(root.get("date"), date));
            }

            // Filtre par lieu
            if (lieu != null && !lieu.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("lieu"), lieu));
            }

            // Combine tous les prédicats avec AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}