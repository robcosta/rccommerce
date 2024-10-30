package rccommerce.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import rccommerce.dto.CategoryDTO;
import rccommerce.dto.CategoryMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.util.AccentUtils;

@Entity
@Table(name = "tb_category", indexes = {
    @Index(name = "idx_category_name_unaccented", columnList = "nameUnaccented")
})
public class Category implements Convertible<CategoryDTO, CategoryMinDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String nameUnaccented;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();

    public Category() {
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
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
        this.name = name.toUpperCase();
        setNameUnaccented(this.name);
    }

    public String getNameUnaccented() {
        return nameUnaccented;
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    public Set<Product> getProducts() {
        return products;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Category other = (Category) obj;
        return Objects.equals(name, other.name);
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
