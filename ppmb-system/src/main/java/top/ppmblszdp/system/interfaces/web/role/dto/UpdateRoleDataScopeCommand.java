package top.ppmblszdp.system.interfaces.web.role.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public record UpdateRoleDataScopeCommand(
    @NotNull(message = "数据范围不能为空") Integer dataScope, List<Long> deptIds) implements Serializable {}
