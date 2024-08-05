package rccommerce.dto;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;

import rccommerce.entities.Client;

public class ClientDTO extends UserDTO {

	@CPF
	private String cpf;

	public ClientDTO(Long id, String name, String email, String password, String cpf) {
		super(id, name, email, password);
		this.cpf = cpf;
	}

	public ClientDTO(Client entity) {
		super(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword());
		cpf = entity.getCpf();
		for(GrantedAuthority role: entity.getRoles()){
			super.getRoles().add(role.getAuthority());
		}
	}

	public String getCpf() {
		return cpf;
	}
}
