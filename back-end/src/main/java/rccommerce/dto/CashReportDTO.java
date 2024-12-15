package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Value;
import rccommerce.entities.enums.PaymentType;

@Builder
@Value
public class CashReportDTO {
    private OperatorDTO operator; //Operador do caixa
    private Instant openTime; // Abertura do caixa
    private Instant closeTime; // Fechamento do caixa
    private Map<PaymentType, BigDecimal> operatorData; //Dados enviados pelo operador
    private PaymentType systemData; // Dados do sistema
    private BigDecimal closingBalance; // Saldo final após o fechamento
    private BigDecimal difference; // Diferença, se houver    
}