package top.ppmblszdp.system.application.service.user;

import java.util.Optional;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;
import top.ppmblszdp.system.interfaces.web.user.dto.UserPageQuery;

public interface UserApplicationService {
  UserDto createUser(CreateUserCommand command);

  UserDto registerUser(top.ppmblszdp.api.system.dto.UserRegisterDto command);

  Optional<UserDto> getUserById(Long id);

  void deleteUser(Long id);

  PageResult<UserDto> pageUsers(UserPageQuery query, PageQuery pageQuery);
}
