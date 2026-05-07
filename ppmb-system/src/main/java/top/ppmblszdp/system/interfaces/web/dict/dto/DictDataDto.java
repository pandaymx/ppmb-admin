package top.ppmblszdp.system.interfaces.web.dict.dto;

import java.time.LocalDateTime;

/** 字典数据 DTO. */
public record DictDataDto(
    Long id,
    Long parentId,
    Integer dictSort,
    String dictLabel,
    String dictValue,
    String dictType,
    String isDefault,
    String listClass,
    Integer status,
    String remark,
    LocalDateTime createTime) {}
