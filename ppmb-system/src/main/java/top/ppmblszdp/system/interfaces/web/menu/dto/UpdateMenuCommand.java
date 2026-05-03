package top.ppmblszdp.system.interfaces.web.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record UpdateMenuCommand(
    @NotBlank(message = "菜单名称不能为空") String menuName,
    @NotNull(message = "父菜单ID不能为空") Long parentId,
    @NotBlank(message = "菜单类型不能为空") String menuType,
    String path,
    String component,
    String perms,
    String icon,
    Integer orderNum,
    Boolean visible)
    implements Serializable {}
