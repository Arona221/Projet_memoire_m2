package connect.event.participant.repository;

import connect.event.participant.entity.BilletAcheter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BilletAcheterRepository extends JpaRepository<BilletAcheter, Long> {
    Optional<BilletAcheter> findByReferenceTransaction(String referenceTransaction);
}
