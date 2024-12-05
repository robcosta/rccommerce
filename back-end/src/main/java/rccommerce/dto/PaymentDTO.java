package rccommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Payment;
import rccommerce.entities.PaymentDetail;

@AllArgsConstructor
@Getter
public class PaymentDTO {

    private Long id;
    private Instant moment;

    @NotNull(message = "O valor não pode ser nulo.")
    private Long orderId;

    @Size(min = 1, message = "Indique pelo menos uma forma de pagemnto válida.")
    private List<PaymentDetailDTO> paymentDetails = new ArrayList<>();

    public PaymentDTO(Payment entity) {
        this.id = entity.getId();
        this.orderId = entity.getOrder().getId();
        for (PaymentDetail paymentDetail : entity.getPaymentDetails()) {
            paymentDetails.add(new PaymentDetailDTO(paymentDetail));
        }
    }

    public List<PaymentDetailDTO> getPaymentDetails() {
        return paymentDetails;
    }
}
