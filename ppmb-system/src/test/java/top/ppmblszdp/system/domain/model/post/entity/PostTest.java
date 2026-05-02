package top.ppmblszdp.system.domain.model.post.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.ppmblszdp.common.exception.BusinessException;

@DisplayName("岗位领域实体单元测试")
class PostTest {

  @Test
  @DisplayName("创建岗位成功")
  void createPost_success() {
    Post post = Post.create("P001", "Software Engineer", 1, 0, "Remark");
    assertNotNull(post);
    assertEquals("P001", post.getPostCode());
    assertEquals("Software Engineer", post.getPostName());
    assertEquals(1, post.getSortNum());
    assertEquals(0, post.getStatus());
    assertEquals("Remark", post.getRemark());
    assertEquals(0, post.getDelFlag());
  }

  @Test
  @DisplayName("创建岗位失败-编码或名称为空")
  void createPost_fail() {
    assertThrows(BusinessException.class, () -> Post.create("", "Name", 1, 0, null));
    assertThrows(BusinessException.class, () -> Post.create("Code", "", 1, 0, null));
  }

  @Test
  @DisplayName("更新岗位成功")
  void updatePost_success() {
    Post post = Post.create("P001", "Software Engineer", 1, 0, "Remark");
    post.update("P002", "Senior Engineer", 2, 1, "New Remark");
    assertEquals("P002", post.getPostCode());
    assertEquals("Senior Engineer", post.getPostName());
    assertEquals(2, post.getSortNum());
    assertEquals(1, post.getStatus());
    assertEquals("New Remark", post.getRemark());
  }

  @Test
  @DisplayName("禁用和启用岗位成功")
  void enableDisablePost_success() {
    Post post = Post.create("P001", "Name", 1, 0, null);
    post.disable();
    assertEquals(1, post.getStatus());
    post.enable();
    assertEquals(0, post.getStatus());
  }

  @Test
  @DisplayName("删除岗位成功")
  void deletePost_success() {
    Post post = Post.create("P001", "Name", 1, 0, null);
    post.delete();
    assertEquals(1, post.getDelFlag());
  }

  @Test
  @DisplayName("测试 Equals 和 HashCode")
  void testEqualsAndHashCode() {
    Post p1 = Post.create("P1", "N1", 1, 0, null);
    Post p2 = Post.create("P1", "N1", 1, 0, null);

    assertEquals(p1, p1);
    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
    assertNotEquals(null, p1);
    assertNotEquals("string", p1);
  }
}
