package top.ppmblszdp.system.interfaces.web.dept.dto;

import java.io.Serializable;

/**
 * 部门 DTO.
 *
 * @param id 部门 ID
 * @param parentId 父部门 ID
 * @param deptName 部门名称
 * @param deptCode 部门编码
 * @param abbreviation 简称
 * @param email 邮箱
 * @param phone 电话
 * @param leaderId 负责人 ID
 * @param sortNum 排序
 * @param status 状态 (0=正常, 1=停用)
 */
public record DepartmentDto(
    Long id,
    Long parentId,
    String deptName,
    String deptCode,
    String abbreviation,
    String email,
    String phone,
    Long leaderId,
    Integer sortNum,
    Integer status)
    implements Serializable {}
