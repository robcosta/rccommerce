package rccommerce.dto;

import jakarta.validation.constraints.Size;
import rccommerce.entities.Client;

public class ClientDTO extends UserDTO {

	@Size(min = 11, max = 14, message = "CPF precisa ter de 3 a 80 caracteres")
	private String cpf;

	public ClientDTO(Long id, String name, String email, String password, String cpf) {
		super(id, name, email, password);
		this.cpf = cpf;
	}

	public ClientDTO(Client entity) {
		super(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword());
		cpf = entity.getCpf();
		super.getRoles().add("ROLE_CLIENT");
	}

	public String getCpf() {
		return cpf;
	}
}
