package top.ppmblszdp.system.infrastructure.persistence.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.model.role.entity.Role;

/** 角色 JPA 仓储. */
@Repository
public interface RoleJpaRepository
    extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {}
