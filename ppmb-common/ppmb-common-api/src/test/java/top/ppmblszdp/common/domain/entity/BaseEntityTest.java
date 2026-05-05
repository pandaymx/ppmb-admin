package top.ppmblszdp.common.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.domain.generator.SnowflakeIdentifierGenerator;

@DisplayName("基础实体基类单元测试")
class BaseEntityTest {

  @Test
  @DisplayName("验证雪花算法生成器")
  void testSnowflakeGenerator() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);

    Object id1 = generator.generate(session, new TestEntity());
    Object id2 = generator.generate(session, new TestEntity());

    assertThat(id1).isNotNull().isInstanceOf(Long.class);
    assertThat(id2).isNotNull().isInstanceOf(Long.class);
    assertThat(id1).isNotEqualTo(id2);
  }

  @Test
  @DisplayName("验证 Getter 和 Setter")
  void testGettersAndSetters() {
    LocalDateTime now = LocalDateTime.now();
    TestEntity entity = new TestEntity();
    entity.setId(1L);
    entity.setTenantId(10L);
    entity.setCreateTime(now);
    entity.setCreateBy(100L);
    entity.setDeptId(200L);
    entity.setRoleId(300L);
    entity.setDataScope(1);

    assertThat(entity.getId()).isEqualTo(1L);
    assertThat(entity.getTenantId()).isEqualTo(10L);
    assertThat(entity.getCreateTime()).isEqualTo(now);
    assertThat(entity.getCreateBy()).isEqualTo(100L);
    assertThat(entity.getDeptId()).isEqualTo(200L);
    assertThat(entity.getRoleId()).isEqualTo(300L);
    assertThat(entity.getDataScope()).isEqualTo(1);
    assertThat(entity.toString()).contains("id=1");
    assertThat(entity.hashCode()).isNotZero();
    assertThat(entity).isNotNull();
    assertThat(entity).isNotEqualTo(new TestEntity());
  }

  static class TestEntity extends BaseEntity {}
}
