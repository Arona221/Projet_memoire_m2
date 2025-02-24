package connect.event.admin.repository;

import connect.event.admin.entity.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RessourceRepository extends JpaRepository<Ressource, Long>, JpaSpecificationExecutor<Ressource> {
}
