package top.ppmblszdp.system.service.impl;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.domain.entity.Post;
import top.ppmblszdp.system.repository.PostRepository;
import top.ppmblszdp.system.service.PostService;

/** 岗位管理服务实现. */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Post createPost(PostDto postDto) {
    checkUnique(postDto, null);
    Post post =
        Post.create(
            postDto.postCode(),
            postDto.postName(),
            postDto.sortNum(),
            postDto.status(),
            postDto.remark());
    return postRepository.save(post);
  }

  @Override
  public Post getPostById(Long id) {
    return postRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, CommonResultCode.USER_ERROR, "岗位不存在", null));
  }

  @Override
  public PageResult<Post> getPostPage(PostQuery query) {
    Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());

    Specification<Post> spec =
        (root, _, cb) -> {
          List<Predicate> predicates = new ArrayList<>();

          if (StringUtils.hasText(query.getPostCode())) {
            predicates.add(cb.equal(root.get("postCode"), query.getPostCode()));
          }
          if (StringUtils.hasText(query.getPostName())) {
            predicates.add(cb.like(root.get("postName"), "%" + query.getPostName() + "%"));
          }
          if (query.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), query.getStatus()));
          }

          // ignore soft deleted
          predicates.add(cb.equal(root.get("delFlag"), 0));

          return cb.and(predicates.toArray(new Predicate[0]));
        };

    Page<Post> page = postRepository.findAll(spec, pageable);
    return PageResult.of(
        page.getTotalElements(), page.getContent(), query.getPageNum(), query.getPageSize());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Post updatePost(Long id, PostDto postDto) {
    Post post = getPostById(id);
    checkUnique(postDto, id);

    post.update(
        postDto.postCode(),
        postDto.postName(),
        postDto.sortNum(),
        postDto.status(),
        postDto.remark());

    return postRepository.save(post);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePost(Long id) {
    Post post = getPostById(id);
    post.delete();
    postRepository.save(post);
  }

  @Override
  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  private void checkUnique(PostDto postDto, Long id) {
    if (id == null) {
      if (postRepository.existsByPostCode(postDto.postCode())) {
        throw new BusinessException("岗位编码已存在: " + postDto.postCode());
      }
      if (postRepository.existsByPostName(postDto.postName())) {
        throw new BusinessException("岗位名称已存在: " + postDto.postName());
      }
    } else {
      if (postRepository.existsByPostCodeAndIdNot(postDto.postCode(), id)) {
        throw new BusinessException("岗位编码已存在: " + postDto.postCode());
      }
      if (postRepository.existsByPostNameAndIdNot(postDto.postName(), id)) {
        throw new BusinessException("岗位名称已存在: " + postDto.postName());
      }
    }
  }
}
