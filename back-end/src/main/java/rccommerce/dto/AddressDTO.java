package rccommerce.dto;

// ...existing code...
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Address;

@NoArgsConstructor
@Getter
public class AddressDTO {

    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private String state;
    private String zipCode;

    public AddressDTO(Address entity) {
        this.street = entity.getStreet();
        this.number = entity.getNumber();
        this.complement = entity.getComplement();
        this.district = entity.getDistrict();
        this.city = entity.getCity();
        this.state = entity.getState();
        this.zipCode = entity.getZipCode();
    }

    public String getZipCode() {
        return zipCode.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }
}
