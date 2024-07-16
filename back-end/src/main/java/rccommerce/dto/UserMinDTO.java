package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import rccommerce.entity.User;

public class UserMinDTO {
	private Long id;
	private String name;
	private String email;
	private Double commission;

	private List<String> roles = new ArrayList<>();

	public UserMinDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		commission = entity.getCommission();

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

	public List<String> getRoles() {
		return roles;
	}
}
