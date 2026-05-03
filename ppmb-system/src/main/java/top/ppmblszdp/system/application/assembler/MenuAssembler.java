package top.ppmblszdp.system.application.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.interfaces.web.menu.dto.CreateMenuCommand;
import top.ppmblszdp.system.interfaces.web.menu.dto.MenuDto;
import top.ppmblszdp.system.interfaces.web.menu.dto.UpdateMenuCommand;

@Mapper(componentModel = "spring")
public interface MenuAssembler {

  @org.mapstruct.Mapping(target = "id", ignore = true)
  @org.mapstruct.Mapping(target = "tenantId", ignore = true)
  @org.mapstruct.Mapping(target = "createTime", ignore = true)
  @org.mapstruct.Mapping(target = "createBy", ignore = true)
  @org.mapstruct.Mapping(target = "createByDept", ignore = true)
  @org.mapstruct.Mapping(target = "updateBy", ignore = true)
  @org.mapstruct.Mapping(target = "updateTime", ignore = true)
  @org.mapstruct.Mapping(target = "delFlag", ignore = true)
  @org.mapstruct.Mapping(target = "version", ignore = true)
  @org.mapstruct.Mapping(target = "children", ignore = true)
  SysMenu toEntity(CreateMenuCommand command);

  @org.mapstruct.Mapping(target = "id", ignore = true)
  @org.mapstruct.Mapping(target = "tenantId", ignore = true)
  @org.mapstruct.Mapping(target = "createTime", ignore = true)
  @org.mapstruct.Mapping(target = "createBy", ignore = true)
  @org.mapstruct.Mapping(target = "createByDept", ignore = true)
  @org.mapstruct.Mapping(target = "updateBy", ignore = true)
  @org.mapstruct.Mapping(target = "updateTime", ignore = true)
  @org.mapstruct.Mapping(target = "delFlag", ignore = true)
  @org.mapstruct.Mapping(target = "version", ignore = true)
  @org.mapstruct.Mapping(target = "children", ignore = true)
  void updateEntity(@MappingTarget SysMenu entity, UpdateMenuCommand command);

  MenuDto toDto(SysMenu entity);
}
