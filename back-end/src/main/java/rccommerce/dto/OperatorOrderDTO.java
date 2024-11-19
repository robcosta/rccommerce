package rccommerce.dto;

import java.math.BigDecimal;

import rccommerce.entities.Operator;

public class OperatorOrderDTO {

    private Long id;
    private String name;
    private BigDecimal commission;

    public OperatorOrderDTO(Long id, String name, BigDecimal commission) {
        this.id = id;
        this.name = name;
        this.commission = commission;
    }

    public OperatorOrderDTO(Operator entity) {
        id = entity.getId();
        name = entity.getName();
        commission = entity.getCommission();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCommission() {
        return commission;
    }
}
