package rccommerce.services.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityContextUtil {

    // Método para obter o userId do token JWT
    public static Long getUserId() {
        Long userId = -1L; // Valor padrão se o userId não for encontrado

        // Obtém o objeto de autenticação atual do SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se o principal contém o token JWT
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Extrai o userId do token JWT
            userId = jwt.getClaim("userId");
        }

        return userId;
    }

//	// Método para obter a lista de authorities (roles e permissions) do usuário
//	// autenticado
//	public static List<String> getAuthList() {
//		// Obtém o objeto de autenticação atual do SecurityContext
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//		// Obtém a coleção de authorities do usuário autenticado
//		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//		// Converte a coleção de GrantedAuthority em uma lista de Strings com os nomes
//		// das authorities
//		return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
//	}
    public static List<String> getAuthList() {
        // Obtem o objeto de autenticação atual do SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica o tipo de Authentication
        if (authentication == null) {
            System.out.println("Authentication is null");
            return List.of("ROLE_ANONYMOUS");
        } else {
            System.out.println("Authentication class: " + authentication.getClass().getSimpleName());
            System.out.println("Authorities: " + authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }

        // Obtém a coleção de authorities do usuário autenticado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Converte a coleção de GrantedAuthority em uma lista de Strings com os nomes das authorities
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
