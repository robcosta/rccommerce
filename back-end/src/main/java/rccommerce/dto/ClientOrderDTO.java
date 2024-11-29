package rccommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Client;

@AllArgsConstructor
@Getter
public class ClientOrderDTO {

    private Long id;
    private String name;

    public ClientOrderDTO(Client entity) {
        id = entity.getId();
        name = entity.getName();
    }
}
