package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import rccommerce.controllers.validators.EmailCustom;
import rccommerce.controllers.validators.Password;
import rccommerce.entities.Permission;
import rccommerce.entities.User;

public class UserDTO {

	private Long id;
	@NotBlank(message = "Campo requerido")
	@Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
	private String name;

	@NotBlank(message = "Campo requerido")
	@EmailCustom
	private String email;

	@Size(min = 4, max = 8, message = "Senha: mínimo de 4 e máximo de 8 caracteres.")
	@Password(message = "Senha deve conter apenas números.")
	private String password;

	private List<String> roles = new ArrayList<>();

//	@EnumExists(enumClass = PermissionAuthority.class, message = "Um ou mais valores no campo 'verifiedXXXXXX' não são válidos.")
	private List<String> permissions = new ArrayList<>();

	public UserDTO(Long id, String name, String email, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public UserDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		password = entity.getPassword();
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

	public String getPassword() {
		return password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public List<String> getPermissions() {
		return permissions;
	}
}
