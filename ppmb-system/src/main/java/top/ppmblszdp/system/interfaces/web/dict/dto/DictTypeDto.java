package top.ppmblszdp.system.interfaces.web.dict.dto;

import java.time.LocalDateTime;

/** 字典类型 DTO. */
public record DictTypeDto(
    Long id,
    String dictName,
    String dictType,
    String systemFlag,
    Integer status,
    String remark,
    LocalDateTime createTime,
    LocalDateTime updateTime) {}
