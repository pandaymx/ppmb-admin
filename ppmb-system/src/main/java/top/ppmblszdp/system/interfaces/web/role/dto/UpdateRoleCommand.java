package top.ppmblszdp.system.interfaces.web.role.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record UpdateRoleCommand(
    @NotBlank(message = "角色名称不能为空") String roleName, String description, List<Long> menuIds)
    implements Serializable {}
