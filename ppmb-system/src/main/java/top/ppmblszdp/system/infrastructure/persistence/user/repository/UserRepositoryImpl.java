package top.ppmblszdp.system.infrastructure.persistence.user.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userJpaRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userJpaRepository.findByUsername(username);
  }

  @Override
  public void deleteById(Long id) {
    userJpaRepository.deleteById(id);
  }
}
