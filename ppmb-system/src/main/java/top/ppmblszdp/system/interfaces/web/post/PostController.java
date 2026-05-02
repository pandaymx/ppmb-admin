package top.ppmblszdp.system.interfaces.web.post;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.Result;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;
import top.ppmblszdp.system.application.service.post.PostApplicationService;

/** 岗位管理控制器. */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostApplicationService postService;

  @PostMapping
  public Result<PostDto> createPost(@Validated @RequestBody PostDto postDto) {
    return Result.success(postService.createPost(postDto));
  }

  @GetMapping("/{id}")
  public Result<PostDto> getPostById(@PathVariable Long id) {
    return Result.success(postService.getPostById(id));
  }

  @GetMapping("/page")
  public Result<PageResult<PostDto>> getPostPage(PostQuery query) {
    return Result.success(postService.getPostPage(query));
  }

  @GetMapping
  public Result<List<PostDto>> getAllPosts() {
    return Result.success(postService.getAllPosts());
  }

  @PutMapping("/{id}")
  public Result<PostDto> updatePost(
      @PathVariable Long id, @Validated @RequestBody PostDto postDto) {
    return Result.success(postService.updatePost(id, postDto));
  }

  @DeleteMapping("/{id}")
  public Result<Void> deletePost(@PathVariable Long id) {
    postService.deletePost(id);
    return Result.success();
  }
}
