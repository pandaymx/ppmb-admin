package top.ppmblszdp.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("JSON 工具类测试")
class JsonUtilTest {

  /** 测试用数据记录. */
  private record TestObj(String name, int age) {}

  @Test
  @DisplayName("应该能够将对象转换为 JSON 字符串")
  void shouldConvertObjectToJsonString() {
    TestObj obj = new TestObj("John", 30);
    String json = JsonUtil.toJsonString(obj);
    assertEquals("{\"name\":\"John\",\"age\":30}", json, "生成的 JSON 字符串内容不匹配");
  }

  @Test
  @DisplayName("应该能够将 JSON 字符串解析为对象")
  void shouldParseJsonStringToObject() {
    String json = "{\"name\":\"Jane\",\"age\":25}";
    TestObj obj = JsonUtil.parseObject(json, TestObj.class);
    assertNotNull(obj, "解析后的对象不应为空");
    assertEquals("Jane", obj.name(), "解析后的姓名不匹配");
    assertEquals(25, obj.age(), "解析后的年龄不匹配");
  }

  @Test
  @DisplayName("解析非法 JSON 时应抛出业务异常")
  void shouldThrowBusinessExceptionWhenJsonIsInvalid() {
    String invalidJson = "{invalid}";
    assertThrows(
        BusinessException.class,
        () -> JsonUtil.parseObject(invalidJson, TestObj.class),
        "非法 JSON 应该抛出 BusinessException");
  }

  @Test
  @DisplayName("调用私有构造方法应抛出 UnsupportedOperationException")
  void shouldThrowExceptionWhenInstantiating() throws Exception {
    java.lang.reflect.Constructor<JsonUtil> constructor = JsonUtil.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    java.lang.reflect.InvocationTargetException exception =
        assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
    assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    assertEquals(
        "This is a utility class and cannot be instantiated", exception.getCause().getMessage());
  }

  static class SelfRef {
    public SelfRef self = this;
  }

  @Test
  @DisplayName("序列化失败时应抛出业务异常")
  void shouldThrowBusinessExceptionWhenSerializationFails() {
    SelfRef selfRef = new SelfRef();
    assertThrows(
        BusinessException.class,
        () -> JsonUtil.toJsonString(selfRef),
        "序列化失败应该抛出 BusinessException");
  }
}
