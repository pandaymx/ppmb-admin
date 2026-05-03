package top.ppmblszdp.common.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("岗位数据传输对象单元测试")
class PostDtoTest {

  @Test
  @DisplayName("测试 PostDto 的创建和属性访问")
  void testPostDtoCreationAndAccessors() {
    Long id = 1L;
    String postCode = "P001";
    String postName = "Software Engineer";
    Integer sortNum = 1;
    Integer status = 0;
    String remark = "Remark";

    PostDto postDto = new PostDto(id, postCode, postName, sortNum, status, remark);

    assertEquals(id, postDto.id());
    assertEquals(postCode, postDto.postCode());
    assertEquals(postName, postDto.postName());
    assertEquals(sortNum, postDto.sortNum());
    assertEquals(status, postDto.status());
    assertEquals(remark, postDto.remark());
  }

  @Test
  @DisplayName("测试 PostDto 的 Equals 和 HashCode")
  void testEqualsAndHashCode() {
    PostDto dto1 = new PostDto(1L, "P1", "N1", 1, 0, "R");
    PostDto dto2 = new PostDto(1L, "P1", "N1", 1, 0, "R");
    PostDto dto3 = new PostDto(2L, "P1", "N1", 1, 0, "R");

    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1, dto3);
    assertNotEquals(null, dto1);
    assertNotEquals("string", dto1);
  }

  @Test
  @DisplayName("测试 PostDto 的 ToString")
  void testToString() {
    PostDto dto = new PostDto(1L, "P1", "N1", 1, 0, "R");
    String toString = dto.toString();
    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("postCode=P1"));
    assertTrue(toString.contains("postName=N1"));
  }
}
