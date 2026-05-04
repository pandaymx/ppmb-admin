package top.ppmblszdp.api.system.dto;

import java.io.Serializable;

/** 用户注册 DTO. */
public record UserRegisterDto(String username, String password, String email, String nickname)
    implements Serializable {
  private static final long serialVersionUID = 1L;
}
