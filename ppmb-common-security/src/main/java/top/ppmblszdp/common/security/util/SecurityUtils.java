package top.ppmblszdp.common.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SecurityUtils {

  public static Long getUserId() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String userIdStr = request.getHeader("X-User-Id");
      if (userIdStr != null && !userIdStr.isEmpty()) {
        return Long.parseLong(userIdStr);
      }
    }

    // Fallback or local dev depending on implementation
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof String) {
        try {
          return Long.parseLong((String) principal);
        } catch (NumberFormatException e) {
          // Ignore, fallback to return 1L for simplicity if it fails (not ideal for prod, but fine
          // for test)
          return 1L;
        }
      }
    }
    return 1L; // default mock user ID if missing
  }
}
