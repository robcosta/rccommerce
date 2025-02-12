package rccommerce.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.entities.Tax;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxDTO {
    private Long id;
    
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

    public TaxDTO(Tax tax) {        
        this.cstIcms = tax.getCstIcms();
        this.csosn = tax.getCsosn();
        this.icms = tax.getIcms();
        this.ipi = tax.getIpi();
        this.pis = tax.getPis();
        this.cofins = tax.getCofins();
        this.ncm = tax.getNcm();
        this.cest = tax.getCest();
        this.cfop = tax.getCfop();
        this.icmsOrigem = tax.getIcmsOrigem();
        this.icmsSt = tax.getIcmsSt();
        this.ean = tax.getEan();
        this.cstPis = tax.getCstPis();
        this.cstCofins = tax.getCstCofins();
        this.cstIpi = tax.getCstIpi();
        this.pisBase = tax.getPisBase();
        this.cofinsBase = tax.getCofinsBase();
        this.icmsBase = tax.getIcmsBase();
        this.icmsStBase = tax.getIcmsStBase();
        this.ipiBase = tax.getIpiBase();
        this.mva = tax.getMva();
        this.tipoCalculoIcms = tax.getTipoCalculoIcms();
        this.tipi = tax.getTipi();
        this.enquadramentoIpi = tax.getEnquadramentoIpi();
        this.reducaoBase = tax.getReducaoBase();
        this.diferimento = tax.getDiferimento();
    }
}
