package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import rccommerce.entities.Permission;
import rccommerce.entities.User;

public class UserMinDTO {

	private Long id;
	private String name;
	private String email;

	private List<String> roles = new ArrayList<>();

	private List<String> permissions = new ArrayList<>();

	public UserMinDTO(Long id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public UserMinDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		for (GrantedAuthority role : entity.getRoles()) {
			roles.add(role.getAuthority());
		}
		for (Permission permission : entity.getPermissions()) {
			permissions.add(permission.getAuthority());
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public List<String> getPermissions() {
		return permissions;
	}
}
