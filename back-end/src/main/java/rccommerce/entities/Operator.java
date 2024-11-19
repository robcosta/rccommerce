package rccommerce.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.services.interfaces.Convertible;

@Entity
@Table(name = "tb_operator")
public class Operator extends User implements Convertible<OperatorDTO, OperatorMinDTO> {

    private BigDecimal commission;

    public Operator() {
    }

    public Operator(Long id, String name, String email, String password, BigDecimal commission) {
        super(id, name, email, password);
        this.commission = commission;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    @Override
    public OperatorDTO convertDTO() {
        return new OperatorDTO(this);
    }

    @Override
    public OperatorMinDTO convertMinDTO() {
        return new OperatorMinDTO(this);
    }
}
