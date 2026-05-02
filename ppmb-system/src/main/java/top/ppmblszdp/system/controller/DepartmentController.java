package top.ppmblszdp.system.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.system.domain.entity.Department;
import top.ppmblszdp.system.service.DepartmentService;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
    return ResponseEntity.ok(departmentService.createDepartment(department));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
    return departmentService
        .getDepartmentById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<Department>> getAllDepartments() {
    return ResponseEntity.ok(departmentService.getAllDepartments());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Department> updateDepartment(
      @PathVariable Long id, @RequestBody Department department) {
    try {
      return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    departmentService.deleteDepartment(id);
    return ResponseEntity.noContent().build();
  }
}
