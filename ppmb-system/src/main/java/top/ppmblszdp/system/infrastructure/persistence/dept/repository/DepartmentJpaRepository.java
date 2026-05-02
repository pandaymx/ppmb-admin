package top.ppmblszdp.system.infrastructure.persistence.dept.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.dept.entity.Department;
import top.ppmblszdp.system.domain.model.dept.repository.DepartmentRepository;

/** 部门 JPA 仓储实现. 直接继承 JpaRepository 并实现领域层的 DepartmentRepository 接口. */
@Repository
public interface DepartmentJpaRepository
    extends JpaRepository<Department, Long>, DepartmentRepository {}
