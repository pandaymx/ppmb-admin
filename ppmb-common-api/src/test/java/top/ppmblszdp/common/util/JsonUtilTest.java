package top.ppmblszdp.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JsonUtilTest {

  static class TestObj {
    private String name;
    private int age;

    public TestObj() {}

    public TestObj(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }
  }

  @Test
  void testToJsonString() {
    TestObj obj = new TestObj("John", 30);
    String json = JsonUtil.toJsonString(obj);
    assertEquals("{\"name\":\"John\",\"age\":30}", json);
  }

  @Test
  void testParseObject() {
    String json = "{\"name\":\"Jane\",\"age\":25}";
    TestObj obj = JsonUtil.parseObject(json, TestObj.class);
    assertEquals("Jane", obj.getName());
    assertEquals(25, obj.getAge());
  }
}
