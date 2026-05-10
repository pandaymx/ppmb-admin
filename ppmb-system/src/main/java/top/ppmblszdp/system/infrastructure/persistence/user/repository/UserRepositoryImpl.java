package top.ppmblszdp.system.infrastructure.persistence.user.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository jpaRepository;

  @Override
  public User save(User user) {
    return jpaRepository.save(user);
  }

  @Override
  public Optional<User> findById(Long id) {
    return jpaRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaRepository.findByUsername(username);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public Page<User> findAll(Specification<User> spec, Pageable pageable) {
    return jpaRepository.findAll(spec, pageable);
  }
}
