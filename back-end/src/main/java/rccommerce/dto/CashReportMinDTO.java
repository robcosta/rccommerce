package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import rccommerce.entities.enums.MovementType;

@Builder
@Getter
@Value
public class CashReportMinDTO {

    private OperatorReporter operator; // Operador do caixa
    private Instant openTime; // Abertura do caixa
    private Instant closeTime; // Fechamento do caixa
    private Map<MovementType, BigDecimal> operatorData; // Dados enviados pelo operador
    private Map<MovementType, BigDecimal> systemData; // Dados do sistema
    private BigDecimal amountOperator; // Valor total enviado pelo operador
    private BigDecimal amountSystem; // Valor total informado pelo sistema
    private BigDecimal difference; // Diferença, se houver

    @Builder
    @Getter
    @Value
    public static class OperatorReporter {

        private Long id;
        private String name;
    }
}
