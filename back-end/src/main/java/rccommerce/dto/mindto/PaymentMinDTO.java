package rccommerce.dto.mindto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Payment;

@AllArgsConstructor
@Getter
public class PaymentMinDTO {

    private final Long id;
    private final Instant moment;
    private final Long orderId;
    private final Long cashRegister;
    private String message;

    public PaymentMinDTO(Payment entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.orderId = entity.getOrder().getId();
        this.cashRegister = entity.getCashRegister().getId();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        if (message == null) {
            this.message = "Ok!";
        }
        return message;
    }
}
