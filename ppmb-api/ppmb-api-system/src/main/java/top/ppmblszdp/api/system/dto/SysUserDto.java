package top.ppmblszdp.api.system.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class SysUserDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  private String password;
  private String nickname;
  private String email;
  private String phone;
  private Integer status;
}
