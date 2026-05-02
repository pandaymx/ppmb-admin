package top.ppmblszdp.system.domain.model.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.ppmblszdp.common.api.CommonResultCode;
import top.ppmblszdp.common.domain.entity.BaseMainEntity;
import top.ppmblszdp.common.util.AssertUtils;

/** 用户领域实体. */
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sys_user")
public class User extends BaseMainEntity {

  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;

  @Column(name = "password", nullable = false, length = 100)
  private String password;

  @Column(name = "nickname", length = 50)
  private String nickname;

  @Column(name = "email", length = 100)
  private String email;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "status", nullable = false)
  private Integer status;

  /**
   * 创建用户.
   *
   * @param username 用户名
   * @param password 密码
   * @param nickname 昵称
   * @return 用户实体
   */
  public static User create(String username, String password, String nickname) {
    AssertUtils.notEmpty(username, CommonResultCode.PARAM_ERROR);
    AssertUtils.notEmpty(password, CommonResultCode.PARAM_ERROR);
    User user = new User();
    user.setUsername(username);
    user.setPassword(password);
    user.setNickname(nickname);
    user.setStatus(0); // 正常
    return user;
  }

  /**
   * 更新基本信息.
   *
   * @param nickname 昵称
   * @param email 邮箱
   * @param phone 手机号
   */
  public void updateInfo(String nickname, String email, String phone) {
    this.nickname = nickname;
    this.email = email;
    this.phone = phone;
  }

  /** 禁用用户. */
  public void disable() {
    this.status = 1;
  }

  /** 启用用户. */
  public void enable() {
    this.status = 0;
  }
}
