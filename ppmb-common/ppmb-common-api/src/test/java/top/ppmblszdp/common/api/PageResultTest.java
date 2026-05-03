package top.ppmblszdp.common.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResultTest {

  @Test
  void testOf() {
    List<String> data = List.of("item1", "item2");
    PageResult<String> result = PageResult.of(100L, data, 2, 10);

    assertEquals(100L, result.total());
    assertEquals(data, result.list());
    assertEquals(2, result.pageNum());
    assertEquals(10, result.pageSize());
  }

  @Test
  void testEmpty() {
    PageResult<String> result = PageResult.empty(3, 20);

    assertEquals(0L, result.total());
    assertTrue(result.list().isEmpty());
    assertEquals(3, result.pageNum());
    assertEquals(20, result.pageSize());
  }
}
