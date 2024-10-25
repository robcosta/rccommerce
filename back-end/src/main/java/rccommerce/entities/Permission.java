package rccommerce.entities;

import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import rccommerce.entities.enums.PermissionAuthority;

@Entity
@Table(name = "tb_permission")
public class Permission implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authority; // Ex: PERMISSION_CREATE, PERMISSION_DELETE

    public Permission() {
    }

    public Permission(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public String getPermissionAuthority() {
        return authority;
    }

    public void setPermissionAuthority(PermissionAuthority authority) {
        this.authority = authority.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permission other = (Permission) obj;
        return Objects.equals(id, other.id);
    }
}
