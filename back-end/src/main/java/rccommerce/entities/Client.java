package rccommerce.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.ClientDTO;
import rccommerce.dto.mindto.ClientMinDTO;
import rccommerce.services.interfaces.Convertible;

@Builder(builderMethodName = "clientBuilder")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_client", indexes = {
    @Index(name = "idx_client_cpf", columnList = "cpf")})
public class Client extends User implements Convertible<ClientDTO, ClientMinDTO> {

    @Column(unique = true)
    private String cpf;

    @OneToMany(mappedBy = "client")
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public Client(Long id, String name, String email, String password, String cpf) {
        super(id, name, email, password);
        this.cpf = cpf;
        super.getRoles().add(new Role(4L, "ROLE_CLIENT"));
        this.addresses = new ArrayList<>();
    }

    public void setCpf(String cpf) {
        this.cpf = cpf.replaceAll("[^0-9]", "");
    }

    public List<Address> addAddresses(Address address) {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        // Relacionamento bidirecional
        address.setClient(this);
        addresses.add(address);
        return addresses;
    }

    @Override
    public ClientDTO convertDTO() {
        return new ClientDTO(this);
    }

    @Override
    public ClientMinDTO convertMinDTO() {
        return new ClientMinDTO(this);
    }
}
