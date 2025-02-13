package rccommerce.dto.fulldto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.dto.AddressDTO;
import rccommerce.entities.Suplier;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SuplierFullDTO {

    private Long id;
    private String name;
    private String cnpj;
    private List<AddressDTO> addresses = new ArrayList<>();

    public SuplierFullDTO(Suplier entity) {
        id = entity.getId();
        name = entity.getName();
        cnpj = entity.getCnpj();
        entity.getAddresses().forEach(address -> this.addresses.add(new AddressDTO(address)));
    }

    public String getCnpj() {
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
