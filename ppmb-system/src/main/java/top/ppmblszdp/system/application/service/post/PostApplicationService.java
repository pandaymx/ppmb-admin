package top.ppmblszdp.system.application.service.post;

import java.util.List;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;
import top.ppmblszdp.system.domain.model.post.entity.Post;

public interface PostApplicationService {

  Post createPost(PostDto postDto);

  Post getPostById(Long id);

  PageResult<Post> getPostPage(PostQuery query);

  Post updatePost(Long id, PostDto postDto);

  void deletePost(Long id);

  List<Post> getAllPosts();
}
