package top.ppmblszdp.system.service;

import java.util.List;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;
import top.ppmblszdp.system.domain.entity.Post;

public interface PostService {

  Post createPost(PostDto postDto);

  Post getPostById(Long id);

  PageResult<Post> getPostPage(PostQuery query);

  Post updatePost(Long id, PostDto postDto);

  void deletePost(Long id);

  List<Post> getAllPosts();
}
