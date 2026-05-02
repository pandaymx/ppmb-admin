package top.ppmblszdp.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {}
