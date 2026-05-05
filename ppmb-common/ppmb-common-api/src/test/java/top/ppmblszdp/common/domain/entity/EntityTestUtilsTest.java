package top.ppmblszdp.common.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("实体测试工具类单元测试")
class EntityTestUtilsTest {

  @Test
  @DisplayName("测试 setId 方法")
  void testSetId() {
    BaseEntity entity = new BaseEntity() {};
    EntityTestUtils.setId(entity, 123L);
    assertThat(entity.getId()).isEqualTo(123L);
  }

  @Test
  @DisplayName("测试 setId 方法 - 传入 null")
  void testSetId_Null() {
    org.assertj.core.api.Assertions.assertThatCode(() -> EntityTestUtils.setId(null, 123L))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("测试私有构造函数")
  void testPrivateConstructor() throws Exception {
    Constructor<EntityTestUtils> constructor = EntityTestUtils.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    assertThatThrownBy(constructor::newInstance)
        .isInstanceOf(InvocationTargetException.class)
        .hasCauseInstanceOf(UnsupportedOperationException.class);
  }
}
