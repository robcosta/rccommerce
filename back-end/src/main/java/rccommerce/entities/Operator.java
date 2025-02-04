package rccommerce.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.entities.enums.RoleAuthority;
import rccommerce.entities.interfaces.TranslatableEntity;
import rccommerce.services.interfaces.Convertible2;
import rccommerce.services.util.ValidPassword;

@Builder(builderMethodName = "operatorBuilder")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_operator")
public class Operator extends User implements Convertible2<Operator, OperatorDTO, OperatorMinDTO>, TranslatableEntity {

    private BigDecimal commission;

    public Operator(Long id, String name, String email, String password, BigDecimal commission) {
        super(id, name, email, password);
        this.commission = commission;
    }

    public static Operator createOperator(Long id, String name, String email, String password, BigDecimal commission) {
        Operator operator = new Operator();
        operator.setId(id);
        operator.setName(name);
        operator.setEmail(email);
        operator.setPassword(password);
        operator.setCommission(commission);
        return operator;
    }

    @Override
    public OperatorDTO convertDTO() {
        return new OperatorDTO(this);
    }

    @Override
    public OperatorMinDTO convertMinDTO() {
        return new OperatorMinDTO(this);
    }

    @Override
    public Operator convertEntity(OperatorDTO dto) {
        this.setName(dto.getName());
        this.setEmail(dto.getEmail());
        if (!dto.getPassword().isEmpty()) {
            this.setPassword(ValidPassword.isValidPassword(dto.getPassword()));
        }
        this.setCommission(dto.getCommission());
        this.getRoles().clear();
        if (dto.getRoles() == null || !dto.getRoles().isEmpty()) {
            dto.getRoles().forEach(roleDTO -> this.addRole(Role.from(roleDTO)));
        } else {
            this.addRole(Role.from(RoleAuthority.ROLE_OPERATOR.getName()));
        }
        this.getPermissions().clear();
        if (dto.getPermissions() == null || !dto.getPermissions().isEmpty()) {
            dto.getPermissions().forEach(permissionDTO -> this.addPermission(Permission.from(permissionDTO)));
        } else {
            this.addPermission(Permission.from(PermissionAuthority.PERMISSION_READER.getName()));
        }
        return this;
    }

    @Override
    public String getTranslatedEntityName() {
        return "Operador";
    }
}
