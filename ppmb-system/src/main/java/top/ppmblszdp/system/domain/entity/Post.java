package top.ppmblszdp.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 岗位领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_post")
public class Post extends BaseMainEntity {

  /** 岗位编码. */
  @Column(name = "post_code", nullable = false, unique = true, length = 64)
  private String postCode;

  /** 岗位名称. */
  @Column(name = "post_name", nullable = false, length = 50)
  private String postName;

  /** 排序号. */
  @Column(name = "sort_num")
  private Integer sortNum;

  /** 状态 (0 = 正常, 1 = 停用). */
  @Column(name = "status", nullable = false)
  private Integer status;

  /** 备注. */
  @Column(name = "remark")
  private String remark;

  /**
   * 创建岗位.
   *
   * @param code 岗位编码
   * @param name 岗位名称
   * @param sortNum 排序号
   * @param status 状态
   * @param remark 备注
   * @return 岗位实体
   */
  public static Post create(
      String code, String name, Integer sortNum, Integer status, String remark) {
    AssertUtils.notEmpty(code, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(name, CommonResultCode.PARAM_ERROR);

    Post post = new Post();
    post.setPostCode(code);
    post.setPostName(name);
    post.setSortNum(sortNum != null ? sortNum : 0);
    post.setStatus(status != null ? status : 0);
    post.setRemark(remark);
    post.setDelFlag(0);
    return post;
  }

  /**
   * 更新岗位信息.
   *
   * @param code 岗位编码
   * @param name 岗位名称
   * @param sortNum 排序号
   * @param status 状态
   * @param remark 备注
   */
  public void update(String code, String name, Integer sortNum, Integer status, String remark) {
    AssertUtils.notEmpty(code, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(name, CommonResultCode.PARAM_ERROR);

    this.postCode = code;
    this.postName = name;
    this.sortNum = sortNum;
    if (status != null) {
      this.status = status;
    }
    this.remark = remark;
  }

  /** 禁用岗位. */
  public void disable() {
    this.status = 1;
  }

  /** 启用岗位. */
  public void enable() {
    this.status = 0;
  }

  /** 逻辑删除岗位. */
  public void delete() {
    this.setDelFlag(1);
  }
}
