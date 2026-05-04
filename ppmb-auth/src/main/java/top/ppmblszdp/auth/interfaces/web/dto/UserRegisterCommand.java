package top.ppmblszdp.auth.interfaces.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 用户注册命令. */
public record UserRegisterCommand(
    @NotBlank(message = "用户名不能为空") @Size(min = 4, max = 20, message = "用户名长度必须在 4-20 之间") String username,
    @NotBlank(message = "密码不能为空") @Size(min = 6, max = 20, message = "密码长度必须在 6-20 之间") String password,
    @Email(message = "邮箱格式不正确") String email,
    String nickname) {}
