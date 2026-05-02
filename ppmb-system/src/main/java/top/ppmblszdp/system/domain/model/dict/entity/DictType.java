package top.ppmblszdp.system.domain.model.dict.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 字典类型领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_dict_type")
@SQLDelete(sql = "UPDATE sys_dict_type SET del_flag = 1 WHERE id = ? and version = ?")
@org.hibernate.annotations.SQLRestriction("del_flag = 0")
public class DictType extends BaseMainEntity {

  @Column(name = "dict_name", nullable = false, length = 100)
  private String dictName;

  @Column(name = "dict_type", nullable = false, unique = true, length = 100)
  private String dictType;

  @Column(name = "system_flag", nullable = false, length = 1)
  private String systemFlag;

  @Column(name = "status", nullable = false)
  private Integer status;

  @Column(name = "remark", length = 500)
  private String remark;

  /**
   * 创建字典类型.
   *
   * @param dictName 字典名称
   * @param dictType 字典类型
   * @param systemFlag 系统标识
   * @param status 状态
   * @param remark 备注
   * @return 字典类型实体
   */
  public static DictType create(
      String dictName, String dictType, String systemFlag, Integer status, String remark) {
    AssertUtils.notEmpty(dictName, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(dictType, CommonResultCode.PARAM_ERROR);

    DictType dictTypeEntity = new DictType();
    dictTypeEntity.setDictName(dictName);
    dictTypeEntity.setDictType(dictType);
    dictTypeEntity.setSystemFlag(systemFlag != null ? systemFlag : "N");
    dictTypeEntity.setStatus(status != null ? status : 0);
    dictTypeEntity.setRemark(remark);
    return dictTypeEntity;
  }

  /**
   * 更新字典类型信息.
   *
   * @param dictName 字典名称
   * @param dictType 字典类型
   * @param status 状态
   * @param remark 备注
   */
  public void updateInfo(String dictName, String dictType, Integer status, String remark) {
    if (dictName != null) {
      this.dictName = dictName;
    }
    if (dictType != null) {
      this.dictType = dictType;
    }
    if (status != null) {
      this.status = status;
    }
    this.remark = remark;
  }

  public boolean isSystemFlag() {
    return "Y".equals(this.systemFlag);
  }
}
