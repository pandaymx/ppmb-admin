package top.ppmblszdp.system.domain.model.user.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.ppmblszdp.system.domain.model.user.entity.User;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  void deleteById(Long id);

  Page<User> findAll(Specification<User> spec, Pageable pageable);
}
