package rccommerce.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.AddressDTO;
import rccommerce.dto.AddressMinDTO;
import rccommerce.services.interfaces.Convertible2;
import rccommerce.util.StringCapitalize;

@Builder
@Entity
@Table(name = "tb_address")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address implements Convertible2<Address, AddressDTO, AddressMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String street;

    @Column(length = 10)
    private String number;

    @Column(length = 50)
    private String complement;

    @Column(length = 50)
    private String district;

    @Column(length = 50)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(length = 8)
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "suplier_id", nullable = true)
    private Suplier suplier;

    public void setStreet(String street) {
        this.street = StringCapitalize.words(street);
    }

    public void setNumber(String number) {
        this.number = number != null ? number.toUpperCase() : null;
    }

    public void setComplement(String complement) {
        this.complement = complement != null ? StringCapitalize.words(complement) : null;
    }

    public void setDistrict(String district) {
        this.district = StringCapitalize.words(district);
    }

    public void setCity(String city) {
        this.city = StringCapitalize.words(city);
    }

    public void setState(String state) {
        this.state = state != null ? state.toUpperCase() : null;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode != null ? zipCode.replaceAll("[^0-9]", "") : null;
    }

    @Override
    public AddressDTO convertDTO() {
        return new AddressDTO(this);
    }

    @Override
    public AddressMinDTO convertMinDTO() {
        return new AddressMinDTO(this);
    }

    public static Address from(AddressDTO dto) {
        return new Address().convertEntity(dto);
    }

    @Override
    public Address convertEntity(AddressDTO dto) {
        this.setStreet(dto.getStreet());
        this.setNumber(dto.getNumber());
        this.setComplement(dto.getComplement());
        this.setDistrict(dto.getDistrict());
        this.setCity(dto.getCity());
        this.setState(dto.getState());
        this.setZipCode(dto.getZipCode());
        return this;
    }
}
