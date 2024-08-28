package rccommerce.dto;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import rccommerce.entities.Suplier;

public class SuplierMinDTO {

	private Long id;
	
	@NotBlank(message = "Campo requerido")
	@Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
	private String name;
	
	@NotBlank(message = "Campo requerido")
	@CNPJ
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
