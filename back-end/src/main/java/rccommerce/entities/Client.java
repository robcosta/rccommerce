package rccommerce.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.services.interfaces.Convertible;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_client")
public class Client extends User implements Convertible<ClientDTO, ClientMinDTO> {

    @Column(unique = true)
    private String cpf;

    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();

    public Client(Long id, String name, String email, String password, String cpf) {
        super(id, name, email, password);
        this.cpf = cpf;
        super.getRoles().add(new Role(4L, "ROLE_CLIENT"));
    }

    public void setCpf(String cpf) {
        this.cpf = cpf.replaceAll("[^0-9]", "");
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
