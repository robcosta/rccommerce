package rccommerce.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.ProductMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.services.util.AccentUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_product", indexes = {
    @Index(name = "idx_product_name_unaccented", columnList = "nameUnaccented")
})
public class Product implements Convertible<ProductDTO, ProductMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String nameUnaccented;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String unit;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(precision = 15, scale = 2)
    private String imgUrl;

    @Column(precision = 15, scale = 2)
    private BigDecimal quantity;

    @Column(unique = true)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "suplier_id")
    private Suplier suplier;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "tb_product_category_id",
            joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<ProductCategory> categories = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> itens = new HashSet<>();

    public Product(Long id) {
        this.id = id;
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Product(Long id, String name, String description, String unit, BigDecimal price, String imgUrl, BigDecimal quantity,
            String reference, Suplier suplier) {
        this.id = id;
        this.setName(name);
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
        this.reference = reference;
        this.suplier = suplier;
    }

    public void setName(String name) {
        this.name = name;
        setNameUnaccented(name);
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    public void addCategory(ProductCategory category) {
        categories.add(category);
    }

    public List<Order> getOrders() {
        return itens.stream().map(x -> x.getOrder()).toList();
    }

    @Override
    public ProductDTO convertDTO() {
        return new ProductDTO(this);
    }

    @Override
    public ProductMinDTO convertMinDTO() {
        return new ProductMinDTO(this);
    }
}
