package rccommerce.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.enums.MovementType;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@Getter
@Value
public class MovementDetailDTO {

    private Long id;

    @NotNull(message = "O tipo de pagamento não pode ser nulo.")
    private MovementType movementType;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @NotNull(message = "O valor não pode ser nulo.")
    @DecimalMin(value = "0.00", inclusive = true, message = "O valor não pode ser negativo.")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private BigDecimal amount;

    private PaymentDTO paymentDTO;

    public MovementDetailDTO(MovementDetail entity) {
        this.id = entity.getId();
        this.movementType = entity.getMovementType();
        this.amount = entity.getAmount();
        this.paymentDTO = new PaymentDTO(entity.getPayment());
    }

    public BigDecimal getAmount() {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
