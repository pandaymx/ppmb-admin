package top.ppmblszdp.system.interfaces.web.role.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record RoleDto(
    Long id,
    String roleName,
    String roleCode,
    String description,
    Integer status,
    Boolean isReadonly,
    LocalDateTime createTime)
    implements Serializable {}
