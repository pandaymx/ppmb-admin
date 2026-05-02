package top.ppmblszdp.system.application.assembler;

import java.util.List;
import org.mapstruct.Mapper;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserAssembler {

  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);
}
