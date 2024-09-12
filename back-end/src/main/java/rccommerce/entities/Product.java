package rccommerce.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;
	private String unit;
	private Double price;
	private String imgUrl;

	private Double quantity;

	@Column(unique = true)
	private String reference;

	@ManyToOne
	@JoinColumn(name = "suplier_id")
	private Suplier suplier;

	@ManyToMany
	@JoinTable(name = "tb_product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();

	@OneToMany(mappedBy = "id.product")
	private Set<OrderItem> items = new HashSet<>();

	public Product() {
	}

	public Product(Long id, String name, String description, String unit, Double price, String imgUrl, Double quantity,
			String reference, Suplier suplier) {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Suplier getSuplier() {
		return suplier;
	}

	public void setSuplier(Suplier suplier) {
		this.suplier = suplier;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public Set<OrderItem> getItems() {
		return items;
	}

	public List<Order> getOrders() {
		return items.stream().map(x -> x.gerOrder()).toList();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}
}
