package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import rccommerce.entities.CashMovement;
import rccommerce.entities.MovementDetail;

@Builder
@AllArgsConstructor
@Getter
public class CashMovementDTO {

    private final Long id;

    @NotNull(message = "O tipo de movimento não pode ser nulo.")
    private final String cashMovementType;

    @Builder.Default
    @NotEmpty(message = "A lista de pagamentos não pode ser nula ou vazia.")
    @Valid // Valida cada elemento da lista de acordo com as regras em MovementDetailDTO
    private List<MovementDetailDTO> movementDetails = new ArrayList<>();

    private final String description;
    private final Instant timestamp;
    private Long cashRegisterId;

    /**
     * Construtor que consolida pagamentos recebidos no JSON.
     *
     * @param movementDetails Lista de pagamentos a consolidar.
     */
    public CashMovementDTO(CashMovement entity) {
        this.id = entity.getId();
        this.cashMovementType = entity.getCashMovementType().getName();
        for (MovementDetail movement : entity.getMovementDetails()) {
            this.getMovementDetails().add(new MovementDetailDTO(movement));
        }
        this.description = entity.getDescription();
        this.timestamp = entity.getTimestamp();
        this.cashRegisterId = entity.getCashRegister().getId();
    }

    public void setCashRegisterId(Long cashRegisterId) {
        this.cashRegisterId = cashRegisterId;
    }

    /**
     * Adiciona ou atualiza um pagamento no balanço de movimentação de caixa. Se
     * o tipo de pagamento já existir, acumula o valor no existente. Caso
     * contrário, adiciona um novo pagamento à lista.
     *
     * @param movementDetail o detalhe do pagamento a ser adicionado
     */
    public void addPayment(MovementDetailDTO movementDetail) {
        MovementDetailDTO existingPayment = movementDetails.stream()
                .filter(p -> p.getMovementType().equals(movementDetail.getMovementType()))
                .findFirst()
                .orElse(null);

        if (existingPayment != null) {
            // Atualiza o valor do pagamento existente
            BigDecimal updatedAmount = existingPayment.getAmount().add(movementDetail.getAmount());

            // Remove o pagamento existente e adiciona um atualizado
            movementDetails.remove(existingPayment);
            movementDetails.add(new MovementDetailDTO(
                    existingPayment.getId(),
                    existingPayment.getMovementType(),
                    updatedAmount
            ));
        } else {
            // Adiciona novo pagamento se o tipo não existir
            movementDetails.add(movementDetail);
        }
    }

    public BigDecimal getTotalAmount() {
        return movementDetails.stream()
                .map(MovementDetailDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // /**
    //  * Consolida pagamentos, somando os valores de mesmo MovimentType.
    //  *
    //  * @param movementDetails Lista original de pagamentos.
    //  * @return Lista consolidada de pagamentos.
    //  */
    // private List<MovementDetailDTO> consolidatePayments(List<MovementDetailDTO> movementDetails) {
    //     Map<MovimentType, BigDecimal> consolidated = movementDetails.stream()
    //             .collect(Collectors.toMap(
    //                     MovementDetailDTO::getPaymentType,
    //                     MovementDetailDTO::getAmount,
    //                     BigDecimal::add // Soma os valores dos MovimentType duplicados
    //             ));
    //     // Converte o mapa consolidado de volta para uma lista de MovementDetailDTO
    //     return consolidated.entrySet().stream()
    //             .map(entry -> new MovementDetailDTO(null, entry.getKey(), entry.getValue()))
    //             .collect(Collectors.toList());
    // }
}
