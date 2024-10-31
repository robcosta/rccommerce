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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import rccommerce.dto.SuplierDTO;
import rccommerce.dto.SuplierMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.util.AccentUtils;

@Entity
@Table(name = "tb_suplier", indexes = {
    @Index(name = "idx_suplier_name_unaccented", columnList = "nameUnaccented")
})
public class Suplier implements Convertible<SuplierDTO, SuplierMinDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String nameUnaccented;

    @Column(unique = true)
    private String cnpj;

    @OneToMany(mappedBy = "suplier")
    private Set<Product> products = new HashSet<>();

    public Suplier() {
    }

    public Suplier(Long id, String name) {
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
        this.name = name;
        setNameUnaccented(this.name);
    }

    public String getNameUnaccented() {
        return nameUnaccented;
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj.replaceAll("[^0-9]", "");
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
        Suplier other = (Suplier) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public SuplierDTO convertDTO() {
        return new SuplierDTO(this);
    }

    @Override
    public SuplierMinDTO convertMinDTO() {
        return new SuplierMinDTO(this);
    }
}
