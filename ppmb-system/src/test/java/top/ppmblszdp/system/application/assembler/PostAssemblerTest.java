package top.ppmblszdp.system.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.system.domain.model.post.entity.Post;

@DisplayName("岗位对象映射器测试")
class PostAssemblerTest {

  private final PostAssembler assembler = Mappers.getMapper(PostAssembler.class);

  @Test
  @DisplayName("测试 Entity 转换为 DTO")
  void testToDto() {
    Post post = Post.create("CEO", "首席执行官", 1, 0, "备注");
    PostDto dto = assembler.toDto(post);

    assertNotNull(dto);
    assertEquals("CEO", dto.postCode());
    assertEquals("首席执行官", dto.postName());
    assertEquals(1, dto.sortNum());
    assertEquals(0, dto.status());
    assertEquals("备注", dto.remark());
  }

  @Test
  @DisplayName("测试 Entity 列表转换为 DTO 列表")
  void testToDtoList() {
    Post post1 = Post.create("CEO", "首席执行官", 1, 0, null);
    Post post2 = Post.create("CTO", "首席技术官", 2, 0, null);

    List<PostDto> dtoList = assembler.toDtoList(List.of(post1, post2));

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
  }

  @Test
  @DisplayName("测试 Null 值转换")
  void testNull() {
    assertNull(assembler.toDto(null));
    assertNull(assembler.toDtoList(null));
  }
}
