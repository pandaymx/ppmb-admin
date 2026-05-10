package top.ppmblszdp.system.application.service.user.impl;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ppmblszdp.common.api.PageQuery;
import top.ppmblszdp.common.api.PageResult;
import top.ppmblszdp.common.security.data.DataPermissionSpecification;
import top.ppmblszdp.common.security.data.annotation.DataPermission;
import top.ppmblszdp.system.application.assembler.UserAssembler;
import top.ppmblszdp.system.application.service.user.UserApplicationService;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;
import top.ppmblszdp.system.interfaces.web.user.dto.CreateUserCommand;
import top.ppmblszdp.system.interfaces.web.user.dto.UserDto;
import top.ppmblszdp.system.interfaces.web.user.dto.UserPageQuery;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

  private final UserRepository userRepository;
  private final UserAssembler userAssembler;
  private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto createUser(CreateUserCommand command) {
    User user =
        User.create(
            command.username(), passwordEncoder.encode(command.password()), command.nickname());
    user.updateInfo(command.nickname(), command.email(), command.phone());
    return userAssembler.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public UserDto registerUser(top.ppmblszdp.api.system.dto.UserRegisterDto command) {
    User user =
        User.create(
            command.username(), passwordEncoder.encode(command.password()), command.nickname());
    user.updateInfo(command.nickname(), command.email(), null);
    return userAssembler.toDto(userRepository.save(user));
  }

  @Override
  public Optional<UserDto> getUserById(Long id) {
    return userRepository.findById(id).map(userAssembler::toDto);
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  @DataPermission(permission = "sys:user:list")
  public PageResult<UserDto> pageUsers(UserPageQuery query, PageQuery pageQuery) {
    Pageable pageable = PageRequest.of(pageQuery.pageNum() - 1, pageQuery.pageSize());

    Specification<User> baseSpec =
        (root, cq, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (query.getUsername() != null && !query.getUsername().isEmpty()) {
            predicates.add(cb.like(root.get("username"), "%" + query.getUsername() + "%"));
          }
          if (query.getPhone() != null && !query.getPhone().isEmpty()) {
            predicates.add(cb.like(root.get("phone"), "%" + query.getPhone() + "%"));
          }
          if (query.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), query.getStatus()));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    // 包装数据权限过滤
    Specification<User> spec = new DataPermissionSpecification<>(baseSpec);

    Page<User> page = userRepository.findAll(spec, pageable);

    return PageResult.of(
        page.getTotalElements(),
        page.getContent().stream().map(userAssembler::toDto).toList(),
        pageQuery.pageNum(),
        pageQuery.pageSize());
  }
}
