package rccommerce.entities;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.entities.enums.RoleAuthority;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    private RoleAuthority authority; // Ex: ROLE_SELLER, ROLE_OPERATOR

    public void setAuthority(RoleAuthority authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority != null ? authority.toString() : null;
    }

    public static Role from(String authority) {
        return Role.builder()
                .authority(RoleAuthority.fromValue(authority))
                .build();
    }
}
