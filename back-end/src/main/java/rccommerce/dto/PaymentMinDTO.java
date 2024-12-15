package rccommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.Payment;

@AllArgsConstructor
@Getter
public class PaymentMinDTO {

    private final Long id;
    private final Instant moment;
    private final Long orderId;
    private String message;

    private List<MovementDetailDTO> movementDetails = new ArrayList<>();

    public PaymentMinDTO(Payment entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.orderId = entity.getOrder().getId();
        for (MovementDetail movementDetail : entity.getMovementDetails()) {
            movementDetails.add(new MovementDetailDTO(movementDetail));
        }
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
