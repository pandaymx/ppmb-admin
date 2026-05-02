package top.ppmblszdp.system.interfaces.web.user.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record CreateUserCommand(
    @NotBlank(message = "用户名不能为空") String username,
    @NotBlank(message = "密码不能为空") String password,
    String nickname,
    String email,
    String phone)
    implements Serializable {}
