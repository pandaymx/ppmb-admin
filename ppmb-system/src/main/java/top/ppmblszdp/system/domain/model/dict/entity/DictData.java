package top.ppmblszdp.system.domain.model.dict.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.domain.entity.BaseChildEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 字典数据领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_dict_data")
public class DictData extends BaseChildEntity {

  @Column(name = "dict_sort", nullable = false)
  private Integer dictSort;

  @Column(name = "dict_label", nullable = false, length = 100)
  private String dictLabel;

  @Column(name = "dict_value", nullable = false, length = 100)
  private String dictValue;

  @Column(name = "dict_type", nullable = false, length = 100)
  private String dictType;

  @Column(name = "is_default", nullable = false, length = 1)
  private String isDefault;

  @Column(name = "list_class", length = 100)
  private String listClass;

  @Column(name = "status", nullable = false)
  private Integer status;

  @Column(name = "remark", length = 500)
  private String remark;

  /**
   * 创建字典数据.
   *
   * @param parentId 字典类型ID
   * @param dictSort 排序
   * @param dictLabel 标签
   * @param dictValue 键值
   * @param dictType 类型
   * @param isDefault 是否默认
   * @param listClass 样式
   * @param status 状态
   * @param remark 备注
   * @return 字典数据实体
   */
  @SuppressWarnings("java:S107")
  public static DictData create(
      Long parentId,
      Integer dictSort,
      String dictLabel,
      String dictValue,
      String dictType,
      String isDefault,
      String listClass,
      Integer status,
      String remark) {
    AssertUtils.notNull(parentId, CommonResultCode.PARAM_ERROR);
    AssertUtils.notNull(dictSort, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(dictLabel, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(dictValue, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(dictType, CommonResultCode.PARAM_ERROR);

    DictData data = new DictData();
    data.setParentId(parentId);
    data.setDictSort(dictSort);
    data.setDictLabel(dictLabel);
    data.setDictValue(dictValue);
    data.setDictType(dictType);
    data.setIsDefault(isDefault != null ? isDefault : "N");
    data.setListClass(listClass);
    data.setStatus(status != null ? status : 0);
    data.setRemark(remark);
    return data;
  }

  /**
   * 更新字典数据.
   *
   * @param dictSort 排序
   * @param dictLabel 标签
   * @param dictValue 键值
   * @param dictType 类型
   * @param isDefault 是否默认
   * @param listClass 样式
   * @param status 状态
   * @param remark 备注
   */
  @SuppressWarnings("java:S107")
  public void updateInfo(
      Integer dictSort,
      String dictLabel,
      String dictValue,
      String dictType,
      String isDefault,
      String listClass,
      Integer status,
      String remark) {
    if (dictSort != null) {
      this.dictSort = dictSort;
    }
    if (dictLabel != null) {
      this.dictLabel = dictLabel;
    }
    if (dictValue != null) {
      this.dictValue = dictValue;
    }
    if (dictType != null) {
      this.dictType = dictType;
    }
    if (isDefault != null) {
      this.isDefault = isDefault;
    }
    this.listClass = listClass;
    if (status != null) {
      this.status = status;
    }
    this.remark = remark;
  }
}
