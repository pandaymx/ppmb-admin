package top.ppmblszdp.system.application.service.post.impl;

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
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.common.api.query.PostQuery;
import top.ppmblszdp.common.exception.BusinessException;
import top.ppmblszdp.system.application.assembler.PostAssembler;
import top.ppmblszdp.system.application.service.post.PostApplicationService;
import top.ppmblszdp.system.domain.model.post.entity.Post;
import top.ppmblszdp.system.domain.model.post.repository.PostRepository;

/** 岗位管理服务实现. */
@Service
@RequiredArgsConstructor
public class PostApplicationServiceImpl implements PostApplicationService {

  private final PostRepository postRepository;
  private final PostAssembler postAssembler;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public PostDto createPost(PostDto postDto) {
    checkUnique(postDto, null);
    Post post =
        Post.create(
            postDto.postCode(),
            postDto.postName(),
            postDto.sortNum(),
            postDto.status(),
            postDto.remark());
    return postAssembler.toDto(postRepository.save(post));
  }

  private Post getEntityById(Long id) {
    return postRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, CommonResultCode.USER_ERROR, "岗位不存在", null));
  }

  @Override
  public PostDto getPostById(Long id) {
    return postAssembler.toDto(getEntityById(id));
  }

  @Override
  public PageResult<PostDto> getPostPage(PostQuery query) {
    Pageable pageable = PageRequest.of(query.pageNum() - 1, query.pageSize());

    Specification<Post> spec =
        (root, _, cb) -> {
          List<Predicate> predicates = new ArrayList<>();

          java.util.Optional.ofNullable(query.postCode())
              .filter(org.springframework.util.StringUtils::hasText)
              .ifPresent(v -> predicates.add(cb.equal(root.get("postCode"), v)));

          java.util.Optional.ofNullable(query.postName())
              .filter(org.springframework.util.StringUtils::hasText)
              .ifPresent(v -> predicates.add(cb.like(root.get("postName"), "%" + v + "%")));

          java.util.Optional.ofNullable(query.status())
              .ifPresent(v -> predicates.add(cb.equal(root.get("status"), v)));

          // ignore soft deleted
          predicates.add(cb.equal(root.get("delFlag"), 0));

          return cb.and(predicates.toArray(new Predicate[0]));
        };

    Page<Post> page = postRepository.findAll(spec, pageable);
    return PageResult.of(
        page.getTotalElements(),
        postAssembler.toDtoList(page.getContent()),
        query.pageNum(),
        query.pageSize());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public PostDto updatePost(Long id, PostDto postDto) {
    Post post = getEntityById(id);
    checkUnique(postDto, id);

    post.update(
        postDto.postCode(),
        postDto.postName(),
        postDto.sortNum(),
        postDto.status(),
        postDto.remark());

    return postAssembler.toDto(postRepository.save(post));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePost(Long id) {
    Post post = getEntityById(id);
    post.delete();
    postRepository.save(post);
  }

  @Override
  public List<PostDto> getAllPosts() {
    return postAssembler.toDtoList(postRepository.findAll());
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
