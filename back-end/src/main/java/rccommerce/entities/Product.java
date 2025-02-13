package rccommerce.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.mindto.ProductMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.services.util.AccentUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_product", indexes = {
    @Index(name = "idx_product_name_unaccented", columnList = "nameUnaccented"),
    @Index(name = "idx_product_reference", columnList = "reference"),
    @Index(name = "idx_product_suplier_id", columnList = "suplier_id")
})
public class Product implements Convertible<ProductDTO, ProductMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String nameUnaccented;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String un;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(precision = 15, scale = 2)
    private String imgUrl;

    @Column(precision = 15, scale = 2)
    private BigDecimal quantity;

    @Column(unique = true)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "suplier_id")
    private Suplier suplier;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cstIcms", column = @Column(name = "input_cst_icms", length = 3)),
        @AttributeOverride(name = "csosn", column = @Column(name = "input_csosn", length = 3)),
        @AttributeOverride(name = "icms", column = @Column(name = "input_icms", precision = 5, scale = 2)),
        @AttributeOverride(name = "ipi", column = @Column(name = "input_ipi", precision = 5, scale = 2)),
        @AttributeOverride(name = "pis", column = @Column(name = "input_pis", precision = 5, scale = 2)),
        @AttributeOverride(name = "cofins", column = @Column(name = "input_cofins", precision = 5, scale = 2)),
        @AttributeOverride(name = "ncm", column = @Column(name = "input_ncm", length = 8)),
        @AttributeOverride(name = "cest", column = @Column(name = "input_cest", length = 7)),
        @AttributeOverride(name = "cfop", column = @Column(name = "input_cfop", length = 4)),
        @AttributeOverride(name = "icmsOrigem", column = @Column(name = "input_icms_origem", length = 1)),
        @AttributeOverride(name = "icmsSt", column = @Column(name = "input_icms_st", precision = 5, scale = 2)),
        @AttributeOverride(name = "cstPis", column = @Column(name = "input_cst_pis", length = 2)),
        @AttributeOverride(name = "cstCofins", column = @Column(name = "input_cst_cofins", length = 2)),
        @AttributeOverride(name = "cstIpi", column = @Column(name = "input_cst_ipi", length = 2)),
        @AttributeOverride(name = "pisBase", column = @Column(name = "input_pis_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "cofinsBase", column = @Column(name = "input_cofins_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "icmsBase", column = @Column(name = "input_icms_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "icmsStBase", column = @Column(name = "input_icms_st_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "ipiBase", column = @Column(name = "input_ipi_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "mva", column = @Column(name = "input_mva", precision = 5, scale = 2)),
        @AttributeOverride(name = "tipoCalculoIcms", column = @Column(name = "input_tipo_calculo_icms", length = 1)),
        @AttributeOverride(name = "tipi", column = @Column(name = "input_tipi", length = 8)),
        @AttributeOverride(name = "enquadramentoIpi", column = @Column(name = "input_enquadramento_ipi", length = 3)),
        @AttributeOverride(name = "reducaoBase", column = @Column(name = "input_reducao_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "diferimento", column = @Column(name = "input_diferimento", precision = 5, scale = 2)),
        @AttributeOverride(name = "ean", column = @Column(name = "input_ean", length = 13))
    })
    private Tax inputTax;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cstIcms", column = @Column(name = "output_cst_icms", length = 3)),
        @AttributeOverride(name = "csosn", column = @Column(name = "output_csosn", length = 3)),
        @AttributeOverride(name = "icms", column = @Column(name = "output_icms", precision = 5, scale = 2)),
        @AttributeOverride(name = "ipi", column = @Column(name = "output_ipi", precision = 5, scale = 2)),
        @AttributeOverride(name = "pis", column = @Column(name = "output_pis", precision = 5, scale = 2)),
        @AttributeOverride(name = "cofins", column = @Column(name = "output_cofins", precision = 5, scale = 2)),
        @AttributeOverride(name = "ncm", column = @Column(name = "output_ncm", length = 8)),
        @AttributeOverride(name = "cest", column = @Column(name = "output_cest", length = 7)),
        @AttributeOverride(name = "cfop", column = @Column(name = "output_cfop", length = 4)),
        @AttributeOverride(name = "icmsOrigem", column = @Column(name = "output_icms_origem", length = 1)),
        @AttributeOverride(name = "icmsSt", column = @Column(name = "output_icms_st", precision = 5, scale = 2)),
        @AttributeOverride(name = "cstPis", column = @Column(name = "output_cst_pis", length = 2)),
        @AttributeOverride(name = "cstCofins", column = @Column(name = "output_cst_cofins", length = 2)),
        @AttributeOverride(name = "cstIpi", column = @Column(name = "output_cst_ipi", length = 2)),
        @AttributeOverride(name = "pisBase", column = @Column(name = "output_pis_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "cofinsBase", column = @Column(name = "output_cofins_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "icmsBase", column = @Column(name = "output_icms_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "icmsStBase", column = @Column(name = "output_icms_st_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "ipiBase", column = @Column(name = "output_ipi_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "mva", column = @Column(name = "output_mva", precision = 5, scale = 2)),
        @AttributeOverride(name = "tipoCalculoIcms", column = @Column(name = "output_tipo_calculo_icms", length = 1)),
        @AttributeOverride(name = "tipi", column = @Column(name = "output_tipi", length = 8)),
        @AttributeOverride(name = "enquadramentoIpi", column = @Column(name = "output_enquadramento_ipi", length = 3)),
        @AttributeOverride(name = "reducaoBase", column = @Column(name = "output_reducao_base", precision = 5, scale = 2)),
        @AttributeOverride(name = "diferimento", column = @Column(name = "output_diferimento", precision = 5, scale = 2)),
        @AttributeOverride(name = "ean", column = @Column(name = "output_ean", length = 13))
    })
    private Tax outputTax;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "tb_productid_categoryid",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
            indexes = {
                @Index(name = "idx_product_category_product_id", columnList = "product_id"),
                @Index(name = "idx_product_category_category_id", columnList = "category_id")
            }
    )
    private Set<ProductCategory> categories = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> itens = new HashSet<>();

    public Product(Long id) {
        this.id = id;
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Product(Long id, String name, String description, String unit, BigDecimal price, String imgUrl, BigDecimal quantity,
            String reference, Suplier suplier) {
        this.id = id;
        this.setName(name);
        this.description = description;
        this.un = unit;
        this.price = price;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
        this.reference = reference;
        this.suplier = suplier;
    }

    public void setName(String name) {
        this.name = name;
        setNameUnaccented(name);
    }

    public void setNameUnaccented(String nameUnaccented) {
        this.nameUnaccented = AccentUtils.removeAccents(nameUnaccented);
    }

    public void addCategory(ProductCategory category) {
        categories.add(category);
    }

    public List<Order> getOrders() {
        return itens.stream().map(x -> x.getOrder()).toList();
    }

    @Override
    public ProductDTO convertDTO() {
        return new ProductDTO(this);
    }

    @Override
    public ProductMinDTO convertMinDTO() {
        return new ProductMinDTO(this);
    }
}
