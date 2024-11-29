package rccommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserOrderDTO {

    private Long id;
    private String name;

    public UserOrderDTO(User user) {
        id = user.getId();
        name = user.getName();
    }
}
