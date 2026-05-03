package top.ppmblszdp.system.interfaces.web.role.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record CreateRoleCommand(
    @NotBlank(message = "角色名称不能为空") String roleName,
    @NotBlank(message = "角色编码不能为空") String roleCode,
    String description,
    List<Long> menuIds)
    implements Serializable {}
