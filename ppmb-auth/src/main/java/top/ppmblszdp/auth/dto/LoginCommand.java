package top.ppmblszdp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginCommand {
  @NotBlank(message = "Username cannot be blank") private String username;

  @NotBlank(message = "Password cannot be blank") private String password;
}
