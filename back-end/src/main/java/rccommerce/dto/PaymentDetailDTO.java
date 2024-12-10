package rccommerce.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.PaymentDetail;
import rccommerce.entities.enums.PaymentType;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@AllArgsConstructor
@Getter
public class PaymentDetailDTO {

    private final Long id;

    @NotBlank(message = "Campo requerido")
    private final PaymentType paymentType;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @NotNull(message = "O valor não pode ser nulo.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor deve ser maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private final BigDecimal amount;

    public PaymentDetailDTO(PaymentDetail entity) {
        this.id = entity.getId();
        this.paymentType = entity.getPaymentType();
        this.amount = entity.getAmount();
    }
}
