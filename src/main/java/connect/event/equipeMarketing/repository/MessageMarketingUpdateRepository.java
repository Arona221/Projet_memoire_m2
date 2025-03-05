package connect.event.equipeMarketing.repository;

import connect.event.equipeMarketing.entity.MessageMarketingUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMarketingUpdateRepository extends JpaRepository<MessageMarketingUpdate, Long> {
}
