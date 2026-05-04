package top.ppmblszdp.system.application.service.user;

import java.util.Optional;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

public interface UserApplicationService {
  UserDto createUser(CreateUserCommand command);

  UserDto registerUser(top.ppmblszdp.api.system.dto.UserRegisterDto command);

  Optional<UserDto> getUserById(Long id);

  void deleteUser(Long id);
}
