package rccommerce.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.UserDTO;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.entities.enums.RoleAuthority;
import rccommerce.services.util.AccentUtils;
import rccommerce.util.StringCapitalize;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_user", indexes = {
    @Index(name = "idx_user_name_unaccented", columnList = "nameUnaccented"),
    @Index(name = "idx_user_name_email", columnList = "email")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nameUnaccented;

    @Column(unique = true)
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "tb_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "tb_user_permission", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    public User(Long id) {
        this.id = id;
        this.roles = new HashSet<>();
        this.permissions = new HashSet<>();
        this.orders = new ArrayList<>();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public User(Long id, String name, String email, String password) {
        this.id = id;
        setName(name);
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
        this.permissions = new HashSet<>();
        this.orders = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = StringCapitalize.words(name.trim());
        setNameUnaccented(this.name);
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    public void setEmail(String email) {
        this.email = AccentUtils.removeAccents(email.toLowerCase());
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public boolean hasRole(String roleNmame) {
        for (Role role : roles) {
            if (role.getAuthority().equals(roleNmame)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Adiciona os roles como GrantedAuthority
        authorities.addAll(roles);

        // Adiciona as permissions como GrantedAuthority
        authorities.addAll(permissions);

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User copyDtoToEntity(UserDTO dto) {
        this.setName(dto.getName());
        this.setEmail(dto.getEmail());
        if (!dto.getPassword().isEmpty()) {
            this.password = dto.getPassword();
        }
        this.roles.clear();
        dto.getRoles()
                .forEach(role -> this.addRole(Role.builder()
                .authority(RoleAuthority.fromValue(role)).build()));
        this.permissions.clear();
        dto.getPermissions()
                .forEach(permission -> this.addPermission(Permission.builder()
                .authority(PermissionAuthority.fromValue(permission)).build()));
        return this;
    }
}
