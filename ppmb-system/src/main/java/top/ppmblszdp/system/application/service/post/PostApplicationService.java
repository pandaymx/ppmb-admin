package top.ppmblszdp.system.application.service.post;

import java.util.List;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;

public interface PostApplicationService {

  PostDto createPost(PostDto postDto);

  PostDto getPostById(Long id);

  PageResult<PostDto> getPostPage(PostQuery query);

  PostDto updatePost(Long id, PostDto postDto);

  void deletePost(Long id);

  List<PostDto> getAllPosts();
}
