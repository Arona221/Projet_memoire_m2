package connect.event.equipeMarketing.repository;

import connect.event.equipeMarketing.emuns.StatutCampagne;
import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampagneMarketingUpadateRepository extends JpaRepository<CampagneMarketingUpdate, Long> {



}
