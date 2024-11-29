package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.CashRegister;

@Getter
@AllArgsConstructor
public class CashRegisterMinDTO {

    private final Long id;
    private final BigDecimal balance;
    private final Instant openTime;
    private final Instant closeTime;

    public CashRegisterMinDTO(CashRegister entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
    }
}
