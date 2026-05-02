package top.ppmblszdp.system.application.service.user.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDto createUser(CreateUserCommand command) {
    User user = User.create(command.username(), command.password(), command.nickname());
    user.updateInfo(command.nickname(), command.email(), command.phone());
    return toDto(userRepository.save(user));
  }

  @Override
  public Optional<UserDto> getUserById(Long id) {
    return userRepository.findById(id).map(this::toDto);
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  private UserDto toDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getNickname(),
        user.getEmail(),
        user.getPhone(),
        user.getStatus());
  }
}
