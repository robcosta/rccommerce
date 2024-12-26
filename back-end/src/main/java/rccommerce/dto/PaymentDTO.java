package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.Payment;
import rccommerce.entities.enums.MovementType;

@Builder
@AllArgsConstructor
@Getter
public class PaymentDTO {

    private final Long id;
    private final Instant moment;

    @NotNull(message = "O valor não pode ser nulo.")
    private final Long orderId;

    @Builder.Default
    @NotEmpty(message = "A lista de movimento de caixa não pode ser nula ou vazia.")
    @Valid // Valida cada elemento da lista de acordo com as regras em MovementDetailDTO
    private List<MovementDetailDTO> movementDetails = new ArrayList<>();

    public PaymentDTO(Payment entity) {
        this.id = entity.getId();
        this.orderId = entity.getOrder().getId();
        for (MovementDetail movementDetail : entity.getMovementDetails()) {
            movementDetails.add(new MovementDetailDTO(movementDetail));
        }
        this.moment = entity.getMoment();
    }

    public List<MovementDetailDTO> getMovementDetails() {
        return movementDetails;
    }

    public void addPaymentDetails(MovementDetailDTO movementDetailDTO) {
        movementDetails.add(movementDetailDTO);
    }

    /**
     * Transforma a lista de movementDetails em um conjunto (Set) consolidado
     * por MovimentType.
     *
     * @return Um Set contendo os MovementDetailDTO consolidados.
     */
    public Set<MovementDetailDTO> convertToSetAndConsolidate() {
        // Converte o mapa consolidado em um HashSet
        return consolidated().entrySet().stream()
                .map(entry -> new MovementDetailDTO(null, entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Consolida os detalhes de pagamento, somando os valores para cada tipo de
     * pagamento.
     */
    public Map<MovementType, BigDecimal> consolidated() {
        Map<MovementType, BigDecimal> consolidated = movementDetails.stream()
                .collect(Collectors.toMap(
                        MovementDetailDTO::getMovementType,
                        MovementDetailDTO::getAmount,
                        BigDecimal::add // Soma os valores dos tipos duplicados
                ));
        return consolidated;
    }
}
