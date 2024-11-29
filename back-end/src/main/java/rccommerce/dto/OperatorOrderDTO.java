package rccommerce.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Operator;

@AllArgsConstructor
@Getter
public class OperatorOrderDTO {

    private Long id;
    private String name;
    private BigDecimal commission;

    public OperatorOrderDTO(Operator entity) {
        id = entity.getId();
        name = entity.getName();
        commission = entity.getCommission();
    }
}
