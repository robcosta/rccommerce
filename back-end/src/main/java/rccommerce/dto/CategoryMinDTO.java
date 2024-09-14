package rccommerce.dto;

import rccommerce.entities.Category;

public class CategoryMinDTO {

	private Long id;
	private String name;
	
	public CategoryMinDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public CategoryMinDTO(Category entity) {
		id = entity.getId();
		name = entity.getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
