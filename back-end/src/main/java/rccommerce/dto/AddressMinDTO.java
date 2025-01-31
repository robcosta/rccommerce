package rccommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Address;

@NoArgsConstructor
@Getter
public class AddressMinDTO {

    private String street;
    private String city;
    private String state;

    public AddressMinDTO(Address entity) {
        this.street = entity.getStreet();
        this.city = entity.getCity();
        this.state = entity.getState();
    }
}
