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
import rccommerce.dto.CategoryDTO;
import rccommerce.dto.CategoryMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.services.util.AccentUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_category", indexes = {
    @Index(name = "idx_category_name_unaccented", columnList = "nameUnaccented")
})
public class Category implements Convertible<CategoryDTO, CategoryMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String nameUnaccented;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();

    public Category(Long id, String name) {
        this.id = id;       
        setName(name);
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
        setNameUnaccented(this.name);
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    @Override
    public CategoryDTO convertDTO() {
        return new CategoryDTO(this);
    }

    @Override
    public CategoryMinDTO convertMinDTO() {
        return new CategoryMinDTO(this);
    }
}
