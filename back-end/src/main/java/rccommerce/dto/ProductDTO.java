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
	private Double quantity;
	private String reference;
	private SuplierMinDTO suplier;

	@Size(min = 1, message = "Indique pelo menos uma categoria válida")
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO(Long id, String name, String description, String unit, Double price, String imgUrl, Double quantity,
			String reference, SuplierMinDTO suplier) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.unit = unit;
		this.price = price;
		this.imgUrl = imgUrl;
		this.quantity = quantity;
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
		quantity = entity.getQuantity();
		reference = entity.getReference();
		suplier = new SuplierMinDTO(entity.getSuplier());
		for (Category category : entity.getCategories()) {
			categories.add(new CategoryDTO(category));
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

	public Double getQuantity() {
		return quantity;
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
