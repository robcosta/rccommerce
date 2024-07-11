package rccommerce.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CustomUserUtil {

	public String getLoggerUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
		return jwtPrincipal.getClaim("username");
	}
}
