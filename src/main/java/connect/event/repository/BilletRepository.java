package connect.event.repository;

import connect.event.entity.Billet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilletRepository  extends JpaRepository<Billet, Long> {
}
