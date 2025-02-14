package connect.event.specifications;

import connect.event.entity.Evenement;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class EvenementSpecifications {

    public static Specification<Evenement> withStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Evenement> withNameContaining(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nom")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Evenement> withCategory(Categorie category) {
        return (root, query, cb) -> cb.equal(root.get("categorie"), category);
    }

    public static Specification<Evenement> withDate(LocalDate date) {
        return (root, query, cb) -> cb.equal(root.get("date"), date);
    }

    public static Specification<Evenement> withLocation(String location) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lieu")), "%" + location.toLowerCase() + "%");
    }
}