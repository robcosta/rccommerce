package rccommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.entities.TaxConfiguration;
import rccommerce.entities.enums.TaxType;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxConfigurationDTO {
    private Long id;
    private TaxType type;
    
     @Size(min = 3, max = 3, message = "CST do ICMS deve ter 3 caracteres")
    private String cstIcms;
    
    @Size(min = 3, max = 3, message = "CSOSN deve ter 3 caracteres")
    private String csosn;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal icms;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal ipi;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal pis;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal cofins;
    
    @Pattern(regexp = "^\\d{8}$", message = "NCM deve conter exatamente 8 dígitos")
    private String ncm;
    
    @Pattern(regexp = "^\\d{7}$", message = "CEST deve conter exatamente 7 dígitos")
    private String cest;
    
    @Pattern(regexp = "^\\d{4}$", message = "CFOP deve conter exatamente 4 dígitos")
    private String cfop;
    
    @Pattern(regexp = "^[0-7]$", message = "Origem do ICMS deve ser um dígito entre 0 e 7")
    private String icmsOrigem;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal icmsSt;
    
    @Pattern(regexp = "^\\d{8,14}$", message = "Código EAN deve conter entre 8 e 14 dígitos")
    private String ean;

    @Size(min = 2, max = 2, message = "CST do PIS deve ter 2 caracteres")
    private String cstPis;

    @Size(min = 2, max = 2, message = "CST do COFINS deve ter 2 caracteres")
    private String cstCofins;

    @Size(min = 2, max = 2, message = "CST do IPI deve ter 2 caracteres")
    private String cstIpi;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal pisBase;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal cofinsBase;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal icmsBase;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal icmsStBase;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal ipiBase;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal mva;

    @Pattern(regexp = "^[1-9]$", message = "Tipo de cálculo do ICMS inválido")
    private String tipoCalculoIcms;

    @Pattern(regexp = "^\\d{7}$", message = "TIPI deve conter exatamente 7 dígitos")
    private String tipi;

    @Size(min = 3, max = 3, message = "Código de enquadramento do IPI deve ter 3 caracteres")
    private String enquadramentoIpi;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal reducaoBase;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal diferimento;

    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;

    public TaxConfigurationDTO(TaxConfiguration entity) {
        this.id = entity.getId();
        this.type = entity.getType();
        this.cstIcms = entity.getCstIcms();
        this.cest = entity.getCest();
        this.cfop = entity.getCfop();
        this.icmsOrigem = entity.getIcmsOrigem();
        this.icms = entity.getIcms();
        this.icmsSt = entity.getIcmsSt();
        this.ean = entity.getEan();
        this.cstPis = entity.getCstPis();
        this.cstCofins = entity.getCstCofins();
        this.cstIpi = entity.getCstIpi();
        this.pis = entity.getPis();
        this.cofins = entity.getCofins();
        this.pisBase = entity.getPisBase();
        this.cofinsBase = entity.getCofinsBase();
        this.icmsBase = entity.getIcmsBase();
        this.icmsStBase = entity.getIcmsStBase();
        this.ipi = entity.getIpi();
        this.ipiBase = entity.getIpiBase();
        this.mva = entity.getMva();
        this.tipoCalculoIcms = entity.getTipoCalculoIcms();
        this.tipi = entity.getTipi();
        this.enquadramentoIpi = entity.getEnquadramentoIpi();
        this.reducaoBase = entity.getReducaoBase();
        this.diferimento = entity.getDiferimento();        
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.createdBy = entity.getCreatedBy();
    }
}
