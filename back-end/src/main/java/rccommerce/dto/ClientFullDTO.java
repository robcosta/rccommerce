package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Client;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClientFullDTO extends UserMinDTO {

    private String cpf;
    private List<AddressDTO> addresses = new ArrayList<>();

    public ClientFullDTO(Long id, String name, String email, String cpf) {
        super(id, name, email);
        this.cpf = cpf;
    }

    public ClientFullDTO(Client entity) {
        super(entity);
        cpf = entity.getCpf();
        entity.getAddresses().forEach(address -> this.addresses.add(new AddressDTO(address)));
    }

    public String getCpf() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
