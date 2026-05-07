package top.ppmblszdp.system.interfaces.web.menu.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/** 菜单 DTO. */
public record MenuDto(
    Long id,
    String menuName,
    Long parentId,
    String menuType,
    String path,
    String component,
    String perms,
    String icon,
    Integer orderNum,
    Boolean visible,
    LocalDateTime createTime,
    List<MenuDto> children)
    implements Serializable {}
