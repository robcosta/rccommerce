package rccommerce.dto;

import org.springframework.security.core.GrantedAuthority;

import rccommerce.entities.Client;

public class ClientMinDTO extends UserMinDTO {

	private String cpf;

	public ClientMinDTO(Long id, String name, String email, String cpf) {
		super(id, name, email);
		this.cpf = cpf;
	}

	public ClientMinDTO(Client entity) {
		super(entity.getId(), entity.getName(), entity.getEmail());
		cpf = entity.getCpf();
		for(GrantedAuthority role: entity.getRoles()){
			super.getRoles().add(role.getAuthority());
		}
	}

	public String getCpf() {
		return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}
}
