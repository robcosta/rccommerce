package rccommerce.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.SuplierDTO;
import rccommerce.dto.mindto.SuplierMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.services.util.AccentUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_suplier", indexes = {
    @Index(name = "idx_suplier_name_unaccented", columnList = "nameUnaccented")
})
public class Suplier implements Convertible<SuplierDTO, SuplierMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String nameUnaccented;

    @Column(unique = true)
    private String cnpj;

    @OneToMany(mappedBy = "suplier")
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "suplier", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private Set<Address> addresses = new HashSet<>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Suplier(Long id, String name, String cnpj) {
        this.id = id;
        setName(name);
        setCnpj(cnpj);
        this.products = new HashSet<>();
        this.addresses = new HashSet<>();
    }

    public void setName(String name) {
        this.name = name;
        setNameUnaccented(this.name);
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj.replaceAll("[^0-9]", "");
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public Set<Address> addAddresses(Address address) {
        if (addresses == null) {
            addresses = new HashSet<>();
        }
        // Relacionamento bidirecional
        address.setSuplier(this);
        addresses.add(address);
        return addresses;
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
