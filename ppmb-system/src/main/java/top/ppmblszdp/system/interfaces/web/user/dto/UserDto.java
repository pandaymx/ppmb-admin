package top.ppmblszdp.system.interfaces.web.user.dto;

import java.io.Serializable;

public record UserDto(
    Long id, String username, String nickname, String email, String phone, Integer status)
    implements Serializable {}
