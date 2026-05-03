package top.ppmblszdp.api.system.dto;

import java.io.Serializable;

/** System User Data Transfer Object. */
public record SysUserDto(
    Long id,
    String username,
    String password,
    String nickname,
    String email,
    String phone,
    Integer status)
    implements Serializable {
  private static final long serialVersionUID = 1L;
}
