package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Suplier;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SuplierMinDTO {

    private Long id;
    private String name;
    private String cnpj;
    private List<AddressMinDTO> addresses = new ArrayList<>();

    public SuplierMinDTO(Suplier entity) {
        id = entity.getId();
        name = entity.getName();
        cnpj = entity.getCnpj();
        entity.getAddresses().forEach(address -> this.addresses.add(new AddressMinDTO(address)));
    }

    public String getCnpj() {
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
