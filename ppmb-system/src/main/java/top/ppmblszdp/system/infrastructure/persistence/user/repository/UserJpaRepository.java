package top.ppmblszdp.system.infrastructure.persistence.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.ppmblszdp.system.domain.model.user.entity.User;

public interface UserJpaRepository
    extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  Optional<User> findByUsername(String username);
}
