package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import rccommerce.entities.Payment;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@Getter
public class PaymentMinDTO {

    private Long id;
    private Instant moment;
    private Long orderId;
    private String paymentType;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal amount;

    public PaymentMinDTO(Payment entity) {
        id = entity.getId();
        moment = entity.getMoment();
        paymentType = entity.getPaymentType().getName();
        orderId = entity.getOrder().getId();
        amount = entity.getAmount();
    }
}
