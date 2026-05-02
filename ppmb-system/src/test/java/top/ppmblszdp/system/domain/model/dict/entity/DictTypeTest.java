package top.ppmblszdp.system.domain.model.dict.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("字典类型领域实体单元测试")
class DictTypeTest {

  @Test
  @DisplayName("创建字典类型成功")
  void createDictType_success() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "用户状态字典");

    assertNotNull(dictType);
    assertEquals("用户状态", dictType.getDictName());
    assertEquals("user_status", dictType.getDictType());
    assertEquals("N", dictType.getSystemFlag());
    assertEquals(0, dictType.getStatus());
    assertEquals("用户状态字典", dictType.getRemark());
  }

  @Test
  @DisplayName("创建字典类型失败-名称为空")
  void createDictType_fail_emptyName() {
    assertThrows(
        BusinessException.class,
        () -> DictType.create("", "user_status", "N", 0, null),
        "字典名称为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典类型失败-类型为空")
  void createDictType_fail_emptyType() {
    assertThrows(
        BusinessException.class, () -> DictType.create("用户状态", "", "N", 0, null), "字典类型为空时应抛出异常");
  }

  @Test
  @DisplayName("创建字典类型成功-默认值处理")
  void createDictType_defaultValues() {
    DictType dictType = DictType.create("用户状态", "user_status", null, null, null);

    assertNotNull(dictType);
    assertEquals("N", dictType.getSystemFlag(), "systemFlag 默认为 N");
    assertEquals(0, dictType.getStatus(), "status 默认为 0");
    assertNull(dictType.getRemark());
  }

  @Test
  @DisplayName("更新字典类型信息成功")
  void updateInfo_success() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "备注");

    dictType.updateInfo("用户类型", "user_type", 1, "新备注");

    assertEquals("用户类型", dictType.getDictName());
    assertEquals("user_type", dictType.getDictType());
    assertEquals(1, dictType.getStatus());
    assertEquals("新备注", dictType.getRemark());
  }

  @Test
  @DisplayName("更新字典类型信息-部分字段为null时不更新")
  void updateInfo_partialUpdate() {
    DictType dictType = DictType.create("用户状态", "user_status", "N", 0, "备注");

    dictType.updateInfo(null, null, null, "仅更新备注");

    assertEquals("用户状态", dictType.getDictName(), "名称为null时保持原值");
    assertEquals("user_status", dictType.getDictType(), "类型为null时保持原值");
    assertEquals(0, dictType.getStatus(), "状态为null时保持原值");
    assertEquals("仅更新备注", dictType.getRemark());
  }

  @Test
  @DisplayName("isSystemFlag-系统内置字典返回true")
  void isSystemFlag_true() {
    DictType dictType = DictType.create("系统字典", "sys_dict", "Y", 0, null);

    assertTrue(dictType.isSystemFlag());
  }

  @Test
  @DisplayName("isSystemFlag-非系统内置字典返回false")
  void isSystemFlag_false() {
    DictType dictType = DictType.create("普通字典", "normal_dict", "N", 0, null);

    assertFalse(dictType.isSystemFlag());
  }

  @Test
  @DisplayName("测试 Equals 和 HashCode")
  void testEqualsAndHashCode() {
    DictType d1 = DictType.create("用户状态", "user_status", "N", 0, null);
    DictType d2 = DictType.create("用户状态", "user_status", "N", 0, null);

    assertEquals(d1, d1);
    assertEquals(d1, d2);
    assertEquals(d1.hashCode(), d2.hashCode());
    assertNotEquals(null, d1);
    assertNotEquals("string", d1);
  }
}
