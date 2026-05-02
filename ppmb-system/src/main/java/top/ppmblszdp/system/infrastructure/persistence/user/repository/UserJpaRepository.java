package top.ppmblszdp.system.infrastructure.persistence.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import top.ppmblszdp.system.domain.model.user.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
