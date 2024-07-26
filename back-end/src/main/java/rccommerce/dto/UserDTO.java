package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import rccommerce.entities.User;

public class UserDTO {
	private Long id;
	@NotBlank(message = "Campo requerido")
	@Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres")
	private String name;
	@NotBlank(message = "Campo requerido")
	@Email
	private String email;
	@PositiveOrZero(message = "Comissão deve ter um valor zero ou positivo")
	private Double commission;
	
	//Validation performed in the UserService
	private String password;
	
	@Size(min = 1, message = "Necessário indicar pelo menos um nível de acesso")
	private List<String> roles = new ArrayList<>();

	public UserDTO(Long id, String name, String email, Double commission, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.commission = commission;
		this.password = password;
	}
	
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
