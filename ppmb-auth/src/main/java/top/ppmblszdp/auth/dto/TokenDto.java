package top.ppmblszdp.auth.dto;

/** Token response data transfer object. */
public record TokenDto(String accessToken, Long expiresIn) {}
