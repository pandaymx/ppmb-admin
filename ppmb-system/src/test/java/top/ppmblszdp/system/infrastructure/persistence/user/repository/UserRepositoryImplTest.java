package top.ppmblszdp.system.infrastructure.persistence.user.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.ppmblszdp.system.domain.model.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户仓储实现测试")
class UserRepositoryImplTest {

  @Mock private UserJpaRepository userJpaRepository;

  @InjectMocks private UserRepositoryImpl userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = User.create("testuser", "password123", "Tester");
  }

  @Test
  @DisplayName("保存用户")
  void save() {
    when(userJpaRepository.save(user)).thenReturn(user);
    User result = userRepository.save(user);
    assertEquals("testuser", result.getUsername());
    verify(userJpaRepository).save(user);
  }

  @Test
  @DisplayName("根据 ID 查找用户")
  void findById() {
    when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));
    Optional<User> result = userRepository.findById(1L);
    assertTrue(result.isPresent());
    assertEquals("testuser", result.get().getUsername());
  }

  @Test
  @DisplayName("根据用户名查找用户")
  void findByUsername() {
    when(userJpaRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    Optional<User> result = userRepository.findByUsername("testuser");
    assertTrue(result.isPresent());
    assertEquals("testuser", result.get().getUsername());
  }

  @Test
  @DisplayName("根据 ID 删除用户")
  void deleteById() {
    doNothing().when(userJpaRepository).deleteById(1L);
    userRepository.deleteById(1L);
    verify(userJpaRepository).deleteById(1L);
  }
}
