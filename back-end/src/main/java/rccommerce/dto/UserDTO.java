package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import rccommerce.entity.User;

public class UserDTO {
	private Long id;
	private String name;
	private String email;
	private Double commission;
	private String password;
	
	private List<String> roles = new ArrayList<>();

	public UserDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		commission = entity.getCommission();
		password = entity.getPassword();
		for(GrantedAuthority role: entity.getRoles()){
			roles.add(role.getAuthority());
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

	public Double getCommission() {
		return commission;
	}
	
	public String getPassword() {
		return password;
	}

	public List<String> getRoles() {
		return roles;
	}
}
