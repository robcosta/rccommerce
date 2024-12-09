package rccommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Payment;
import rccommerce.entities.PaymentDetail;

@AllArgsConstructor
@Getter
public class PaymentMinDTO {

    private Long id;
    private Instant moment;
    private Long orderId;
    private String message;

    private List<PaymentDetailDTO> paymentDetails = new ArrayList<>();

    public PaymentMinDTO(Payment entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.orderId = entity.getOrder().getId();
        for (PaymentDetail paymentDetail : entity.getPaymentDetails()) {
            paymentDetails.add(new PaymentDetailDTO(paymentDetail));
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
