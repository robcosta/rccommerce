package rccommerce.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tax {

    @Column(length = 3)
    private String cstIcms;        // Código da Situação Tributária do ICMS

    @Column(length = 3)
    private String csosn;          // Código de Situação da Operação do Simples Nacional

    @Column(precision = 5, scale = 2)
    private BigDecimal icms;       // Alíquota do ICMS

    @Column(precision = 5, scale = 2)
    private BigDecimal ipi;        // Alíquota do IPI

    @Column(precision = 5, scale = 2)
    private BigDecimal pis;        // Alíquota do PIS

    @Column(precision = 5, scale = 2)
    private BigDecimal cofins;     // Alíquota do COFINS

    @Column(length = 8)
    private String ncm;            // Nomenclatura Comum do Mercosul

    @Column(length = 7)
    private String cest;           // Código Especificador da Substituição Tributária

    @Column(length = 4)
    private String cfop;           // Código Fiscal de Operações e Prestações

    @Column(length = 1)
    private String icmsOrigem;     // Origem do ICMS (0-Nacional, 1-Importação Direta, etc)

    @Column(precision = 5, scale = 2)
    private BigDecimal icmsSt;     // Alíquota do ICMS de Substituição Tributária

    @Column(length = 14)
    private String ean;            // Código EAN/GTIN do produto (opcional)

    @Column(length = 2)
    private String cstPis;         // Código da Situação Tributária do PIS

    @Column(length = 2)
    private String cstCofins;      // Código da Situação Tributária do COFINS

    @Column(length = 2)
    private String cstIpi;         // Código da Situação Tributária do IPI

    @Column(precision = 5, scale = 2)
    private BigDecimal pisBase;    // Base de cálculo do PIS

    @Column(precision = 5, scale = 2)
    private BigDecimal cofinsBase; // Base de cálculo do COFINS

    @Column(precision = 5, scale = 2)
    private BigDecimal icmsBase;   // Base de cálculo do ICMS

    @Column(precision = 5, scale = 2)
    private BigDecimal icmsStBase; // Base de cálculo do ICMS ST

    @Column(precision = 5, scale = 2)
    private BigDecimal ipiBase;    // Base de cálculo do IPI

    @Column(precision = 5, scale = 2)
    private BigDecimal mva;        // Margem de Valor Agregado para ST

    @Column(length = 1)
    private String tipoCalculoIcms;// Tipo de cálculo do ICMS (percentual, valor fixo, etc)

    @Column(length = 7)
    private String tipi;           // Código TIPI (Tabela de Incidência do IPI)

    @Column(length = 3)
    private String enquadramentoIpi; // Código de Enquadramento Legal do IPI
    
    @Column(precision = 5, scale = 2)
    private BigDecimal reducaoBase;  // Percentual de redução da base de cálculo

    @Column(precision = 5, scale = 2)
    private BigDecimal diferimento;  // Percentual de diferimento
}
