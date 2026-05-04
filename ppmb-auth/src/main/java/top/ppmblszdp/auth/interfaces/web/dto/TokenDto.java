package top.ppmblszdp.auth.interfaces.web.dto;

/** Token response data transfer object. */
public record TokenDto(String accessToken, Long expiresIn) {}
