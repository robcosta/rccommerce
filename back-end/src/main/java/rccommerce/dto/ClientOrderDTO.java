package rccommerce.dto;

import rccommerce.entities.Client;

public class ClientOrderDTO {
	
	private Long id;
	private String name;
	
	public ClientOrderDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public ClientOrderDTO(Client entity) {
		id = entity.getId();
		name = entity.getName();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
