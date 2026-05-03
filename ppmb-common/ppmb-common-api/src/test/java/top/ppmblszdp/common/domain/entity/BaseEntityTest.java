package top.ppmblszdp.common.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BaseEntity 及 EntityTestUtils 测试")
class BaseEntityTest {

  private static class TestEntity extends BaseEntity {}

  @Test
  @DisplayName("测试 BaseEntity 基础字段及 setId")
  void testBaseEntityFields() {
    TestEntity entity = new TestEntity();
    entity.setId(100L);
    entity.setTenantId(1L);
    entity.setCreateBy(1L);
    entity.setCreateTime(LocalDateTime.now());
    entity.setDeptId(2L);
    entity.setRoleId(3L);
    entity.setDataScope(1);

    assertEquals(100L, entity.getId());
    assertEquals(1L, entity.getTenantId());
    assertEquals(1L, entity.getCreateBy());
    assertNotNull(entity.getCreateTime());
    assertEquals(2L, entity.getDeptId());
    assertEquals(3L, entity.getRoleId());
    assertEquals(1, entity.getDataScope());
  }

  @Test
  @DisplayName("测试 EntityTestUtils.setId")
  void testEntityTestUtils() {
    TestEntity entity = new TestEntity();
    EntityTestUtils.setId(entity, 200L);
    assertEquals(200L, entity.getId());

    // 测试 null 实体
    EntityTestUtils.setId(null, 300L);
  }
}
