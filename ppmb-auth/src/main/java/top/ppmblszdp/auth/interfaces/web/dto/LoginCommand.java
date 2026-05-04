package top.ppmblszdp.auth.interfaces.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Login command containing user credentials. */
public record LoginCommand(
    @NotBlank(message = "Username cannot be blank") String username,
    @NotBlank(message = "Password cannot be blank") String password) {}
