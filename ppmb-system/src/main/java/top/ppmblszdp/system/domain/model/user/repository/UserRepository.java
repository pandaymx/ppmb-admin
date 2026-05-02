package top.ppmblszdp.system.domain.model.user.repository;

import java.util.Optional;
import top.ppmblszdp.system.domain.model.user.entity.User;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  void deleteById(Long id);
}
