package top.ppmblszdp.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
