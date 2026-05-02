package top.ppmblszdp.system.domain.model.dict.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("字典数据领域实体单元测试")
class DictDataTest {

  @Test
  @DisplayName("创建字典数据成功")
  void createDictData_success() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "Y", "success", 0, "启用状态");

    assertNotNull(data);
    assertEquals(1L, data.getParentId());
    assertEquals(1, data.getDictSort());
    assertEquals("启用", data.getDictLabel());
    assertEquals("1", data.getDictValue());
    assertEquals("user_status", data.getDictType());
    assertEquals("Y", data.getIsDefault());
    assertEquals("success", data.getListClass());
    assertEquals(0, data.getStatus());
    assertEquals("启用状态", data.getRemark());
  }

  @Test
  @DisplayName("创建字典数据失败-父ID为空")
  void createDictData_fail_nullParentId() {
    assertThrows(
        BusinessException.class,
        () -> DictData.create(null, 1, "启用", "1", "user_status", "N", null, 0, null),
        "父ID为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典数据失败-排序为空")
  void createDictData_fail_nullSort() {
    assertThrows(
        BusinessException.class,
        () -> DictData.create(1L, null, "启用", "1", "user_status", "N", null, 0, null),
        "排序为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典数据失败-标签为空")
  void createDictData_fail_emptyLabel() {
    assertThrows(
        BusinessException.class,
        () -> DictData.create(1L, 1, "", "1", "user_status", "N", null, 0, null),
        "标签为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典数据失败-键值为空")
  void createDictData_fail_emptyValue() {
    assertThrows(
        BusinessException.class,
        () -> DictData.create(1L, 1, "启用", "", "user_status", "N", null, 0, null),
        "键值为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典数据失败-类型为空")
  void createDictData_fail_emptyType() {
    assertThrows(
        BusinessException.class,
        () -> DictData.create(1L, 1, "启用", "1", "", "N", null, 0, null),
        "类型为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典数据成功-默认值处理")
  void createDictData_defaultValues() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", null, null, null, null);

    assertNotNull(data);
    assertEquals("N", data.getIsDefault(), "isDefault 默认为 N");
    assertEquals(0, data.getStatus(), "status 默认为 0");
    assertNull(data.getListClass());
    assertNull(data.getRemark());
  }

  @Test
  @DisplayName("更新字典数据成功")
  void updateInfo_success() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");

    data.updateInfo(2, "禁用", "0", "user_type", "Y", "danger", 1, "新备注");

    assertEquals(2, data.getDictSort());
    assertEquals("禁用", data.getDictLabel());
    assertEquals("0", data.getDictValue());
    assertEquals("user_type", data.getDictType());
    assertEquals("Y", data.getIsDefault());
    assertEquals("danger", data.getListClass());
    assertEquals(1, data.getStatus());
    assertEquals("新备注", data.getRemark());
  }

  @Test
  @DisplayName("更新字典数据-部分字段为null时不更新")
  void updateInfo_partialUpdate() {
    DictData data = DictData.create(1L, 1, "启用", "1", "user_status", "N", "success", 0, "备注");

    data.updateInfo(null, null, null, null, null, "warning", null, "仅更新样式和备注");

    assertEquals(1, data.getDictSort(), "排序为null时保持原值");
    assertEquals("启用", data.getDictLabel(), "标签为null时保持原值");
    assertEquals("1", data.getDictValue(), "键值为null时保持原值");
    assertEquals("user_status", data.getDictType(), "类型为null时保持原值");
    assertEquals("N", data.getIsDefault(), "是否默认为null时保持原值");
    assertEquals("warning", data.getListClass());
    assertEquals(0, data.getStatus(), "状态为null时保持原值");
    assertEquals("仅更新样式和备注", data.getRemark());
  }

  @Test
  @DisplayName("测试 Equals 和 HashCode")
  void testEqualsAndHashCode() {
    DictData d1 = DictData.create(1L, 1, "启用", "1", "user_status", "N", null, 0, null);
    DictData d2 = DictData.create(1L, 1, "启用", "1", "user_status", "N", null, 0, null);

    assertEquals(d1, d1);
    assertEquals(d1, d2);
    assertEquals(d1.hashCode(), d2.hashCode());
    assertNotEquals(null, d1);
    assertNotEquals("string", d1);
  }
}
