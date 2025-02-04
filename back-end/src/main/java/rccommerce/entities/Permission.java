package rccommerce.entities;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
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
import rccommerce.entities.enums.PermissionAuthority;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_permission")
public class Permission implements GrantedAuthority {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionAuthority authority; // Ex: PERMISSION_CREATE, PERMISSION_DELETE

    public void setPermissionAuthority(PermissionAuthority authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority.name();
    }

    public static Permission from(String authority) {
        return Permission.builder()
                .authority(PermissionAuthority.fromValue(authority))
                .build();
    }
}
