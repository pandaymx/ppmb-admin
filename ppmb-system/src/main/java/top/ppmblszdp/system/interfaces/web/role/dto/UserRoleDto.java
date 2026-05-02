package top.ppmblszdp.system.interfaces.web.role.dto;

import java.io.Serializable;
import java.util.List;

public record UserRoleDto(Long userId, List<Long> roleIds) implements Serializable {}
