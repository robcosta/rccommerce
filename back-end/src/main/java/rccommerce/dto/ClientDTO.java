package rccommerce.dto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Client;

@NoArgsConstructor
@Getter
public class ClientDTO extends UserDTO {

    @CPF
    private String cpf;
    private List<AddressDTO> addresses = new ArrayList<>();

    public ClientDTO(Long id, String name, String email, String password, String cpf) {
        super(id, name, email, password);
        this.cpf = cpf;
    }

    public ClientDTO(Client entity) {
        super(entity);
        cpf = entity.getCpf();
        entity.getAddresses().forEach(address -> this.addresses.add(new AddressDTO(address)));
    }

    public String getCpf() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public List<AddressDTO> addAddresses(AddressDTO address) {
        addresses.add(address);
        return addresses;
    }
}
