package rccommerce.dto;

import rccommerce.entities.Suplier;

public class SuplierMinDTO {

	private Long id;
	private String name;
	private String cnpj;

	public SuplierMinDTO(Long id, String name, String cnpj) {
		this.id = id;
		this.name = name;
		this.cnpj = cnpj;
	}
	
	public SuplierMinDTO(Suplier entity) {
		id = entity.getId();
		name = entity.getName();
		cnpj = entity.getCnpj();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCnpj() {
		return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
	}
}
