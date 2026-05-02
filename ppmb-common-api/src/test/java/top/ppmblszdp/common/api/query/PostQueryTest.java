package top.ppmblszdp.common.api.query;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("岗位查询参数单元测试")
class PostQueryTest {

  @Test
  @DisplayName("测试 PostQuery 的创建和默认值")
  void testPostQueryCreationAndDefaults() {
    // 测试正常赋值
    PostQuery query1 = new PostQuery(2, 20, "P001", "Name", 0);
    assertEquals(2, query1.pageNum());
    assertEquals(20, query1.pageSize());
    assertEquals("P001", query1.postCode());
    assertEquals("Name", query1.postName());
    assertEquals(0, query1.status());

    // 测试默认值
    PostQuery query2 = new PostQuery(null, null, "P001", null, null);
    assertEquals(1, query2.pageNum(), "pageNum 默认值应为 1");
    assertEquals(10, query2.pageSize(), "pageSize 默认值应为 10");
    assertEquals("P001", query2.postCode());
    assertNull(query2.postName());
    assertNull(query2.status());
  }

  @Test
  @DisplayName("测试 PostQuery 的 Equals 和 HashCode")
  void testEqualsAndHashCode() {
    PostQuery q1 = new PostQuery(1, 10, "P1", "N1", 0);
    PostQuery q2 = new PostQuery(1, 10, "P1", "N1", 0);
    PostQuery q3 = new PostQuery(1, 10, "P2", "N1", 0);

    assertEquals(q1, q2);
    assertEquals(q1.hashCode(), q2.hashCode());
    assertNotEquals(q1, q3);
  }

  @Test
  @DisplayName("测试 PostQuery 的 ToString")
  void testToString() {
    PostQuery q = new PostQuery(1, 10, "P1", "N1", 0);
    String toString = q.toString();
    assertTrue(toString.contains("pageNum=1"));
    assertTrue(toString.contains("postCode=P1"));
  }
}
