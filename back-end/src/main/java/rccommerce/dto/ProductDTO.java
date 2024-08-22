package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import rccommerce.entities.Category;
import rccommerce.entities.Product;

public class ProductDTO {

	private Long id;

	@NotBlank(message = "Campo requerido")
	@Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
	private String name;

	private String description;

	@NotBlank(message = "Campo requerido")
	@Size(min = 2, max = 2, message = "Unidade com 2 caracteres.")
	private String unit;

	@Positive(message = "Informe o preço do produto")
	private Double price;
	private String imgUrl;
	private String reference;
	private String suplier;

	@Size(min=1, message = "Indique pelo menos uma categoria válida")
	private List<String> categories = new ArrayList<>();

	public ProductDTO(Long id, String name, String description, String unit, Double price, String imgUrl,
			String reference, String suplier) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.unit = unit;
		this.price = price;
		this.imgUrl = imgUrl;
		this.reference = reference;
		this.suplier = suplier;
	}

	public ProductDTO(Product entity) {
		id = entity.getId();
		name = entity.getName();
		description = entity.getDescription();
		unit = entity.getUnit();
		price = entity.getPrice();
		imgUrl = entity.getImgUrl();
		reference = entity.getReference();
		suplier = entity.getSuplier().getName();
		for(Category category : entity.getCategories()) {
			categories.add(category.getName());
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public Double getPrice() {
		return price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public String getReference() {
		return reference;
	}

	public String getSuplier() {
		return suplier;
	}

	public List<String> getCategories() {
		return categories;
	}
}
