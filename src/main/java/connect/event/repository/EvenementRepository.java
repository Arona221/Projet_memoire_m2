package connect.event.repository;

import connect.event.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository <Evenement, Long>{
}
