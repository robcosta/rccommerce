package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import rccommerce.entities.User;
import rccommerce.entities.Verify;
import rccommerce.entities.enums.Very;

public class UserMinDTO {

	private Long id;
	private String name;
	private String email;

	private List<String> roles = new ArrayList<>();

	private List<Very> very = new ArrayList<>();

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
		for (Verify verify : entity.getVerified()) {
			very.add(verify.getVery());
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

	public List<Very> getVery() {
		return very;
	}
}
