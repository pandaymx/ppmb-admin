package top.ppmblszdp.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.system.domain.entity.Post;
import top.ppmblszdp.system.service.PostService;

@ExtendWith(MockitoExtension.class)
@DisplayName("岗位管理控制器单元测试")
class PostControllerTest {

  private MockMvc mockMvc;

  @Mock private PostService postService;

  @InjectMocks private PostController postController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private Post post;
  private PostDto postDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

    post = Post.create("P001", "Software Engineer", 1, 0, "Remark");
    // Reflection to set ID since it's private and usually set by JPA
    try {
      java.lang.reflect.Field idField =
          top.ppmblszdp.common.domain.entity.BaseEntity.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(post, 1L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    postDto = new PostDto(1L, "P001", "Software Engineer", 1, 0, "Remark");
  }

  @Test
  @DisplayName("创建岗位成功")
  void createPost_success() throws Exception {
    when(postService.createPost(any(PostDto.class))).thenReturn(post);

    mockMvc
        .perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.postCode").value("P001"))
        .andExpect(jsonPath("$.data.postName").value("Software Engineer"));
  }

  @Test
  @DisplayName("根据 ID 获取岗位成功")
  void getPostById_success() throws Exception {
    when(postService.getPostById(1L)).thenReturn(post);

    mockMvc
        .perform(get("/posts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.postCode").value("P001"));
  }

  @Test
  @DisplayName("分页查询岗位成功")
  void getPostPage_success() throws Exception {
    PageResult<Post> pageResult = PageResult.of(1, Arrays.asList(post), 1, 10);
    when(postService.getPostPage(any())).thenReturn(pageResult);

    mockMvc
        .perform(get("/posts/page").param("pageNum", "1").param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.list[0].postCode").value("P001"));
  }

  @Test
  @DisplayName("更新岗位成功")
  void updatePost_success() throws Exception {
    when(postService.updatePost(eq(1L), any(PostDto.class))).thenReturn(post);

    mockMvc
        .perform(
            put("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.postCode").value("P001"));
  }

  @Test
  @DisplayName("删除岗位成功")
  void deletePost_success() throws Exception {
    doNothing().when(postService).deletePost(1L);

    mockMvc.perform(delete("/posts/1")).andExpect(status().isOk());
  }
}
