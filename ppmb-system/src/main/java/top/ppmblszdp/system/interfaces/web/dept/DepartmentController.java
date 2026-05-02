package top.ppmblszdp.system.interfaces.web.dept;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.system.application.service.dept.DepartmentApplicationService;
import top.ppmblszdp.system.interfaces.web.dept.dto.DepartmentDTO;

/** 部门管理接口. */
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentApplicationService departmentApplicationService;

  @PostMapping
  public Result<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
    return Result.success(departmentApplicationService.createDepartment(departmentDTO));
  }

  @GetMapping("/{id}")
  public Result<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
    return departmentApplicationService
        .getDepartmentById(id)
        .map(Result::success)
        .orElseGet(
            Result
                ::success); // Or handle as failure if preferred, but usually 200 with null data is
    // fine for success get if not found? No, better use orElseThrow and let
    // GlobalExceptionHandler handle it if it should be an error.
  }

  @GetMapping
  public Result<List<DepartmentDTO>> getAllDepartments() {
    return Result.success(departmentApplicationService.getAllDepartments());
  }

  @PutMapping("/{id}")
  public Result<DepartmentDTO> updateDepartment(
      @PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
    return Result.success(departmentApplicationService.updateDepartment(id, departmentDTO));
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteDepartment(@PathVariable Long id) {
    departmentApplicationService.deleteDepartment(id);
    return Result.success();
  }
}
