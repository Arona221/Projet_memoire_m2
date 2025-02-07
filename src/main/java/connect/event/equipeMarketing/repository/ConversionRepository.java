package connect.event.equipeMarketing.repository;

import connect.event.equipeMarketing.entity.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionRepository extends JpaRepository<Conversion, Long> {
}
