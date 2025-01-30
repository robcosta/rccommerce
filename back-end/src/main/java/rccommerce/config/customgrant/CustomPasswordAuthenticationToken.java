package rccommerce.config.customgrant;

import java.io.Serial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

public class CustomPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final String username;
    private final String password;
    private final Set<String> scopes;

    public CustomPasswordAuthenticationToken(Authentication clientPrincipal,
            @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {

        super(new AuthorizationGrantType("password"), clientPrincipal, additionalParameters);

        this.userId = additionalParameters != null ? (Long) additionalParameters.get("userId") : null;
        this.username = additionalParameters != null ? (String) additionalParameters.get("username") : null;
        this.password = additionalParameters != null ? (String) additionalParameters.get("password") : null;
        this.scopes = Collections.unmodifiableSet(
                scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Set<String> getScopes() {
        return this.scopes;
    }
}
