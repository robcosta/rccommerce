package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.CashMovement;
import rccommerce.entities.MovementDetail;

@Builder
@AllArgsConstructor
@Getter
@Value
public class CashMovementDTO {

    private Long id;

    @NotNull(message = "O tipo de movimento não pode ser nulo.")
    private String cashMovementType;

    @Builder.Default
    @NotEmpty(message = "A lista de pagamentos não pode ser nula ou vazia.")
    @Valid // Valida cada elemento da lista de acordo com as regras em MovementDetailDTO
    private List<MovementDetailDTO> movementDetails = new ArrayList<>();

    private String description;
    private Instant timestamp;
    private CashRegisterDTO cashRegisterDTO;

    public CashMovementDTO(CashMovement entity) {
        this.id = entity.getId();
        this.cashMovementType = entity.getCashMovementType().getName();
        this.movementDetails = new ArrayList<>();
        for (MovementDetail movementDetail : entity.getMovementDetails()) {
            movementDetails.add(new MovementDetailDTO(movementDetail));
        }
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
        this.cashRegisterDTO = new CashRegisterDTO(entity.getCashRegister());
    }

    public List<MovementDetailDTO> getMovementDetails() {
        return movementDetails.stream()
                .collect(Collectors.toMap(
                        MovementDetailDTO::getMovementType, // Agrupa por movementType
                        MovementDetailDTO::getAmount, // Pega o valor de amount
                        BigDecimal::add // Soma os valores de amount para types duplicados
                ))
                .entrySet().stream() // Converte o mapa de volta para uma lista
                .map(entry -> new MovementDetailDTO(
                null, // ID pode ser null no consolidado
                entry.getKey(), // movementType
                entry.getValue(),// Soma dos amounts
                null // payment pode ser nulo
        ))
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalAmount() {
        return movementDetails.stream()
                .map(MovementDetailDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
