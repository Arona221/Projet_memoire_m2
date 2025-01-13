package connect.event.repository;

import connect.event.entity.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LieuRepository extends JpaRepository<Lieu, Long> {
}
