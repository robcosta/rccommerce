package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import rccommerce.entities.Category;
import rccommerce.entities.Product;

public class ProductMinDTO {

	private Long id;

	@NotBlank(message = "Campo requerido")
	@Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
	private String name;
	
	@NotBlank(message = "Campo requerido")
	@Size(min = 2, max = 2, message = "Unidade com 2 caracteres.")
	private String unit;

	@Positive(message = "Informe o preço do produto")
	private Double price;
	private String imgUrl;
	private String reference;
	private SuplierMinDTO suplier;

	@Size(min=1, message = "Indique pelo menos uma categoria válida")
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductMinDTO(Long id, String name, String unit, Double price, String imgUrl,
			String reference, SuplierMinDTO suplier) {
		this.id = id;
		this.name = name;
		this.unit = unit;
		this.price = price;
		this.imgUrl = imgUrl;
		this.reference = reference;
		this.suplier = suplier;
	}

	public ProductMinDTO(Product entity) {
		id = entity.getId();
		name = entity.getName();
		unit = entity.getUnit();
		price = entity.getPrice();
		imgUrl = entity.getImgUrl();
		reference = entity.getReference();
		suplier = new SuplierMinDTO(entity.getSuplier());
		for(Category category : entity.getCategories()) {
			categories.add(new CategoryDTO(category));
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
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

	public SuplierMinDTO getSuplier() {
		return suplier;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}
}
