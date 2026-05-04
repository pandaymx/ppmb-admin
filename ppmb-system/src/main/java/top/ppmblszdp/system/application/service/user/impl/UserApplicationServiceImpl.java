package top.ppmblszdp.system.application.service.user.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.system.application.assembler.UserAssembler;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

  private final UserRepository userRepository;
  private final UserAssembler userAssembler;
  private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto createUser(CreateUserCommand command) {
    User user =
        User.create(
            command.username(), passwordEncoder.encode(command.password()), command.nickname());
    user.updateInfo(command.nickname(), command.email(), command.phone());
    return userAssembler.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public UserDto registerUser(top.ppmblszdp.api.system.dto.UserRegisterDto command) {
    User user =
        User.create(
            command.username(), passwordEncoder.encode(command.password()), command.nickname());
    user.updateInfo(command.nickname(), command.email(), null);
    return userAssembler.toDto(userRepository.save(user));
  }

  @Override
  public Optional<UserDto> getUserById(Long id) {
    return userRepository.findById(id).map(userAssembler::toDto);
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }
}
