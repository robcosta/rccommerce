package rccommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleDTO {

    private Long id;
    private String authority;

    public RoleDTO(Role entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }
}
