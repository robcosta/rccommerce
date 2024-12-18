package rccommerce.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.enums.MovimentType;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@Getter
@Value
public class MovementDetailDTO {

    private Long id;

    @NotNull(message = "O tipo de pagamento não pode ser nulo.")
    private MovimentType movementType;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @NotNull(message = "O valor não pode ser nulo.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor deve ser maior que zero.")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos na parte inteira e 2 na parte fracionária.")
    private BigDecimal amount;

    public MovementDetailDTO(MovementDetail entity) {
        this.id = entity.getId();
        this.movementType = entity.getMovementType();
        this.amount = entity.getAmount();
    }
}
