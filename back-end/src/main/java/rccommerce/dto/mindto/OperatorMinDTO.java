package rccommerce.dto.mindto;

import java.math.BigDecimal;

import rccommerce.entities.Operator;

public class OperatorMinDTO extends UserMinDTO {

    private BigDecimal commission;

    public OperatorMinDTO(Long id, String name, String email, BigDecimal commission) {
        super(id, name, email);
        this.commission = commission;
    }

    public OperatorMinDTO(Operator entity) {
        super(entity);
        commission = entity.getCommission();
    }

    public BigDecimal getCommission() {
        return commission;
    }
}
