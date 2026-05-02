package top.ppmblszdp.system.application.service.post.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.domain.model.post.entity.Post;
import top.ppmblszdp.system.domain.model.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("岗位管理服务单元测试")
class PostApplicationServiceImplTest {

  @Mock private PostRepository postRepository;

  @InjectMocks private PostApplicationServiceImpl postService;

  private Post post;
  private PostDto postDto;

  @BeforeEach
  void setUp() {
    postDto = new PostDto(null, "P001", "Software Engineer", 1, 0, "Developer");
    post = Post.create("P001", "Software Engineer", 1, 0, "Developer");
  }

  @Test
  @DisplayName("创建岗位成功")
  void createPost_success() {
    when(postRepository.existsByPostCode(postDto.postCode())).thenReturn(false);
    when(postRepository.existsByPostName(postDto.postName())).thenReturn(false);
    when(postRepository.save(any(Post.class))).thenReturn(post);

    Post result = postService.createPost(postDto);

    assertNotNull(result, "创建结果不应为空");
    assertEquals("P001", result.getPostCode(), "岗位编码应一致");
    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("创建岗位失败-岗位编码重复")
  void createPost_duplicatePostCode() {
    when(postRepository.existsByPostCode(postDto.postCode())).thenReturn(true);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> postService.createPost(postDto),
            "应抛出 BusinessException");

    assertTrue(exception.getMessage().contains("岗位编码已存在"), "错误信息不符");
  }

  @Test
  @DisplayName("创建岗位失败-岗位名称重复")
  void createPost_duplicatePostName() {
    when(postRepository.existsByPostCode(postDto.postCode())).thenReturn(false);
    when(postRepository.existsByPostName(postDto.postName())).thenReturn(true);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> postService.createPost(postDto),
            "应抛出 BusinessException");

    assertTrue(exception.getMessage().contains("岗位名称已存在"), "错误信息不符");
  }

  @Test
  @DisplayName("根据 ID 获取岗位成功")
  void getPostById_success() {
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    Post result = postService.getPostById(1L);

    assertNotNull(result, "获取结果不应为空");
  }

  @Test
  @DisplayName("根据 ID 获取岗位失败-不存在")
  void getPostById_notFound() {
    when(postRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        BusinessException.class, () -> postService.getPostById(1L), "应抛出 BusinessException");
  }

  @Test
  @DisplayName("分页查询岗位成功")
  @SuppressWarnings("unchecked")
  void getPostPage_success() {
    PostQuery query = new PostQuery(1, 10, "P001", "Engineer", 0);

    Page<Post> page = new PageImpl<>(Arrays.asList(post));
    when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    PageResult<Post> result = postService.getPostPage(query);

    assertNotNull(result, "查询结果不应为空");
    assertEquals(1, result.total(), "总数应为 1");
    assertEquals(1, result.list().size(), "列表数量应为 1");
  }

  @Test
  @DisplayName("更新岗位成功")
  void updatePost_success() {
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(postRepository.existsByPostCodeAndIdNot(postDto.postCode(), 1L)).thenReturn(false);
    when(postRepository.existsByPostNameAndIdNot(postDto.postName(), 1L)).thenReturn(false);
    when(postRepository.save(any(Post.class))).thenReturn(post);

    Post result = postService.updatePost(1L, postDto);

    assertNotNull(result, "更新结果不应为空");
    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("更新岗位失败-岗位编码重复")
  void updatePost_duplicatePostCode() {
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(postRepository.existsByPostCodeAndIdNot(postDto.postCode(), 1L)).thenReturn(true);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> postService.updatePost(1L, postDto));

    assertTrue(exception.getMessage().contains("岗位编码已存在"));
  }

  @Test
  @DisplayName("更新岗位失败-岗位名称重复")
  void updatePost_duplicatePostName() {
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(postRepository.existsByPostCodeAndIdNot(postDto.postCode(), 1L)).thenReturn(false);
    when(postRepository.existsByPostNameAndIdNot(postDto.postName(), 1L)).thenReturn(true);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> postService.updatePost(1L, postDto));

    assertTrue(exception.getMessage().contains("岗位名称已存在"));
  }

  @Test
  @DisplayName("删除岗位成功")
  void deletePost_success() {
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(postRepository.save(any(Post.class))).thenReturn(post);

    postService.deletePost(1L);

    assertEquals(1, post.getDelFlag(), "删除标志应为 1");
    verify(postRepository).save(post);
  }

  @Test
  @DisplayName("获取所有岗位成功")
  void getAllPosts_success() {
    when(postRepository.findAll()).thenReturn(Arrays.asList(post));

    List<Post> result = postService.getAllPosts();

    assertNotNull(result, "列表不应为空");
    assertEquals(1, result.size(), "数量应为 1");
  }

  @Test
  @DisplayName("测试分页查询 Specification 逻辑")
  @SuppressWarnings("unchecked")
  void testGetPostPageSpecification() {
    PostQuery query = new PostQuery(1, 10, "P001", "Engineer", 0);
    Page<Post> page = new PageImpl<>(List.of(post));
    ArgumentCaptor<Specification<Post>> specCaptor = ArgumentCaptor.forClass(Specification.class);
    when(postRepository.findAll(specCaptor.capture(), any(Pageable.class))).thenReturn(page);

    postService.getPostPage(query);

    // Trigger Specification logic
    final Specification<Post> spec = specCaptor.getValue();
    final jakarta.persistence.criteria.Root<Post> root =
        mock(jakarta.persistence.criteria.Root.class);
    final jakarta.persistence.criteria.Path<Object> path =
        mock(jakarta.persistence.criteria.Path.class);
    lenient().when(root.get(anyString())).thenReturn(path);

    final jakarta.persistence.criteria.CriteriaBuilder cb =
        mock(jakarta.persistence.criteria.CriteriaBuilder.class);
    lenient()
        .when(cb.equal(any(), any()))
        .thenReturn(mock(jakarta.persistence.criteria.Predicate.class));
    lenient()
        .when(cb.like(any(), anyString()))
        .thenReturn(mock(jakarta.persistence.criteria.Predicate.class));
    lenient()
        .when(cb.and(any(jakarta.persistence.criteria.Predicate[].class)))
        .thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

    final jakarta.persistence.criteria.CriteriaQuery<?> criteriaQuery =
        mock(jakarta.persistence.criteria.CriteriaQuery.class);
    jakarta.persistence.criteria.Predicate predicate = spec.toPredicate(root, criteriaQuery, cb);
    assertNotNull(predicate, "生成的 Predicate 不应为空");

    // Test with empty query for remaining branches
    PostQuery emptyQuery = new PostQuery(1, 10, null, null, null);
    postService.getPostPage(emptyQuery);
    jakarta.persistence.criteria.Predicate emptyPredicate =
        specCaptor.getValue().toPredicate(root, criteriaQuery, cb);
    assertNotNull(emptyPredicate, "生成的空 Predicate 不应为空");
  }
}
