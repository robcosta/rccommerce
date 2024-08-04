package rccommerce.dto;

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
		super.getRoles().add("ROLE_CLIENT");
	}

	public String getCpf() {
		return cpf;
	}
}
