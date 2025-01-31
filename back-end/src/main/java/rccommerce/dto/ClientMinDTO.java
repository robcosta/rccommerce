package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Client;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientMinDTO extends UserMinDTO {

    private String cpf;
    private List<AddressMinDTO> addresses = new ArrayList<>();

    public ClientMinDTO(Long id, String name, String email, String cpf) {
        super(id, name, email);
        this.cpf = cpf;
    }

    public ClientMinDTO(Client entity) {
        super(entity);
        cpf = entity.getCpf();
        entity.getAddresses().forEach(address -> this.addresses.add(new AddressMinDTO(address)));
    }

    public String getCpf() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
