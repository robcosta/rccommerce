package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import rccommerce.entities.Payment;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@Getter
public class PaymentDTO {

    private Long id;
    private Instant moment;

    @NotNull(message = "O valor não pode ser nulo.")
    private Long orderId;

    @NotBlank(message = "Campo requerido")
    private String paymentType;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @NotNull(message = "O valor não pode ser nulo.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor deve ser maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private BigDecimal amount;

    public PaymentDTO(Payment entity) {
        paymentType = entity.getPaymentType().getName();
        orderId = entity.getOrder().getId();
        amount = entity.getAmount();
    }
}
