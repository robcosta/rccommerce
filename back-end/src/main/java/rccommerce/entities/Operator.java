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
import rccommerce.dto.mindto.OperatorMinDTO;
import rccommerce.services.interfaces.Convertible;

@Builder(builderMethodName = "operatorBuilder")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_operator")
public class Operator extends User implements Convertible<OperatorDTO, OperatorMinDTO> {

    private BigDecimal commission;

    public Operator(Long id, String name, String email, String password, BigDecimal commission) {
        super(id, name, email, password);
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
