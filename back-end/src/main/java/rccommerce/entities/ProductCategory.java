package rccommerce.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.mindto.ProductCategoryMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.services.util.AccentUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_product_category", indexes = {
    @Index(name = "idx_category_name_unaccented", columnList = "nameUnaccented")
})
public class ProductCategory implements Convertible<ProductCategoryDTO, ProductCategoryMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String nameUnaccented;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ProductCategory(Long id, String name) {
        this.id = id;
        setName(name);
        this.products = new HashSet<>();
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
        setNameUnaccented(this.name);
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    @Override
    public ProductCategoryDTO convertDTO() {
        return new ProductCategoryDTO(this);
    }

    @Override
    public ProductCategoryMinDTO convertMinDTO() {
        return new ProductCategoryMinDTO(this);
    }
}
