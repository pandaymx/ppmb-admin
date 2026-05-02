package top.ppmblszdp.system.application.assembler;

import java.util.List;
import org.mapstruct.Mapper;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.interfaces.web.role.dto.RoleDto;

@Mapper(componentModel = "spring")
public interface RoleAssembler {

  RoleDto toDto(Role role);

  List<RoleDto> toDtoList(List<Role> roles);
}
