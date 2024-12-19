package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Value;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;

@Getter
@Value
public class CashRegisterMinDTO {

    private Long id;
    private BigDecimal balance;
    private Instant openTime;
    private Instant closeTime;
    private List<CashMovementMinDTO> cashMovements = new ArrayList<>();

    public CashRegisterMinDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        for (CashMovement cashMovement : entity.getCashMovements()) {
            getCashMovements().add(new CashMovementMinDTO(cashMovement));
        }

    }
}
