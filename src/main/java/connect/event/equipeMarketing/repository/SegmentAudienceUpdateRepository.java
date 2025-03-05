package connect.event.equipeMarketing.repository;

import connect.event.equipeMarketing.entity.SegmentAudienceUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentAudienceUpdateRepository extends JpaRepository<SegmentAudienceUpdate, Long> {
}
