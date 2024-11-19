package rccommerce.dto;

import java.math.BigDecimal;

import rccommerce.entities.User;

public class UserOrderDTO {

    private Long id;
    private String name;

    public UserOrderDTO(Long id, String name, BigDecimal commission) {
        this.id = id;
        this.name = name;
    }

    public UserOrderDTO(User user) {
        id = user.getId();
        name = user.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
