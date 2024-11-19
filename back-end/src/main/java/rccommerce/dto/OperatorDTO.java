package rccommerce.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import rccommerce.entities.Operator;

public class OperatorDTO extends UserDTO {

    @NotNull(message = "Campo requerido")
    @PositiveOrZero(message = "Comissão deve ter um valor zero ou positivo")
    private BigDecimal commission;

    public OperatorDTO(Long id, String name, String email, String password, BigDecimal commission) {
        super(id, name, email, password);
        this.commission = commission;
    }

    public OperatorDTO(Operator entity) {
        super(entity);
        commission = entity.getCommission();
    }

    public BigDecimal getCommission() {
        return commission;
    }
}
