package top.ppmblszdp.system.interfaces.web.role.dto;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public record BatchUserRoleCommand(
    @NotEmpty(message = "用户 ID 列表不能为空") List<Long> userIds,
    @NotEmpty(message = "角色 ID 列表不能为空") List<Long> roleIds)
    implements Serializable {}
