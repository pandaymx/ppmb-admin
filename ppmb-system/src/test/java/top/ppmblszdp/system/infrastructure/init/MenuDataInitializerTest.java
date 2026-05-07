package top.ppmblszdp.system.infrastructure.init;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import top.ppmblszdp.system.domain.model.menu.entity.SysMenu;
import top.ppmblszdp.system.domain.model.menu.repository.MenuRepository;
import top.ppmblszdp.system.domain.model.menu.repository.RoleMenuRepository;
import top.ppmblszdp.system.domain.model.role.entity.Role;
import top.ppmblszdp.system.domain.model.role.repository.RoleRepository;
import top.ppmblszdp.system.domain.model.role.repository.UserRoleRepository;
import top.ppmblszdp.system.domain.model.user.entity.User;
import top.ppmblszdp.system.domain.model.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("菜单数据初始化器测试")
class MenuDataInitializerTest {

  @Mock private MenuRepository menuRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private RoleMenuRepository roleMenuRepository;
  @Mock private UserRepository userRepository;
  @Mock private UserRoleRepository userRoleRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private MenuDataInitializer menuDataInitializer;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(menuDataInitializer, "adminPassword", "Ppmb@2026");
  }

  @Test
  @DisplayName("当菜单数据不为空时，不执行初始化")
  void shouldNotInitializeWhenDataExists() {
    when(menuRepository.findAll()).thenReturn(List.of(new SysMenu()));

    menuDataInitializer.run();

    verify(menuRepository, never()).save(any(SysMenu.class));
    verify(roleRepository, never()).save(any(Role.class));
  }

  @Test
  @DisplayName("当菜单数据为空时，执行完整初始化流程")
  void shouldInitializeWhenDataIsEmpty() {
    // Setup
    when(menuRepository.findAll()).thenReturn(Collections.emptyList());
    when(roleRepository.findAll()).thenReturn(Collections.emptyList());
    when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

    // Mock saves to return objects with IDs (needed for subsequent steps)
    SysMenu mockMenu = new SysMenu();
    ReflectionTestUtils.setField(mockMenu, "id", 1L);
    when(menuRepository.save(any(SysMenu.class))).thenReturn(mockMenu);

    Role mockRole = Role.create("超级管理员", "admin", "desc");
    ReflectionTestUtils.setField(mockRole, "id", 1L);
    when(roleRepository.save(any(Role.class))).thenReturn(mockRole);

    User mockUser = User.create("admin", "hashed", "admin");
    ReflectionTestUtils.setField(mockUser, "id", 1L);
    when(userRepository.save(any(User.class))).thenReturn(mockUser);

    when(passwordEncoder.encode(anyString())).thenReturn("hashed");

    // Execute
    menuDataInitializer.run();

    // Verify
    verify(menuRepository, times(4)).save(any(SysMenu.class)); // 1 system + 3 submenus
    verify(roleRepository).save(any(Role.class));
    verify(roleMenuRepository).saveAll(anyList());
    verify(userRepository).save(any(User.class));
    verify(userRoleRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("当菜单为空但角色/用户已存在时，仅初始化菜单")
  void shouldOnlyInitializeMenusWhenRolesExist() {
    // Setup
    when(menuRepository.findAll()).thenReturn(Collections.emptyList());
    when(roleRepository.findAll()).thenReturn(List.of(Role.create("admin", "admin", "desc")));

    SysMenu mockMenu = new SysMenu();
    ReflectionTestUtils.setField(mockMenu, "id", 1L);
    when(menuRepository.save(any(SysMenu.class))).thenReturn(mockMenu);

    // Execute
    menuDataInitializer.run();

    // Verify
    verify(menuRepository, times(4)).save(any(SysMenu.class));
    verify(roleRepository, never()).save(any(Role.class));
    verify(userRepository, never()).save(any(User.class));
  }
}
