package rccommerce.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import rccommerce.util.AccentUtils;

@SuppressWarnings("serial")
@Entity
@Table(name = "tb_user", indexes = {
		@Index(name = "idx_user_name_unaccented", columnList = "nameUnaccented")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String nameUnaccented;
	
	@Column(unique = true)
	private String email;
	private String password;

	@ManyToMany
	@JoinTable(name = "tb_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	Set<Role> roles = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "tb_user_verify", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "verify_id"))
	Set<Verify> verified = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private List<Order> orders = new ArrayList<>();

	public User() {
	}

	public User(Long id, String name, String email, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setNameUnaccented(this.name);
	}
	
	public String getNameUnaccented() {
		return nameUnaccented;
	}
	
	public void setNameUnaccented(String nameUnaccented) {
		this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = AccentUtils.removeAccents(email.toLowerCase());
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void addRole(Role role) {
		roles.add(role);
	}

	public Set<Verify> getVerified() {
		return verified;
	}

	public void addVerified(Verify verify) {
		verified.add(verify);
	}

	public List<Order> getOrders() {
		return orders;
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
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
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
}
