package top.ppmblszdp.system.application.assembler;

import java.util.List;
import org.mapstruct.Mapper;
import top.ppmblszdp.system.domain.model.dept.entity.Department;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDto;

@Mapper(componentModel = "spring")
public interface DepartmentAssembler {

  DepartmentDto toDto(Department department);

  List<DepartmentDto> toDtoList(List<Department> departments);
}
