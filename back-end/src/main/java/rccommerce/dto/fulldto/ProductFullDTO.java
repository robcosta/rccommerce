package rccommerce.dto.fulldto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.TaxConfigurationDTO;
import rccommerce.dto.mindto.SuplierMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@NoArgsConstructor
@Getter
public class ProductFullDTO {
    
    private Long id;
    private String name;
    private String description;
    private String unit;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal price;
    
    private String imgUrl;
    
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal quantity;
    
    private String reference;
    private SuplierMinDTO suplier;
    private List<ProductCategoryDTO> categories = new ArrayList<>();
    private TaxConfigurationDTO inputTax;
    private TaxConfigurationDTO outputTax;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;

    public ProductFullDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.unit = entity.getUn();
        this.price = entity.getPrice();
        this.imgUrl = entity.getImgUrl();
        this.quantity = entity.getQuantity();
        this.reference = entity.getReference();
        this.suplier = new SuplierMinDTO(entity.getSuplier());
        
        // Categorias
        for (ProductCategory category : entity.getCategories()) {
            categories.add(new ProductCategoryDTO(category));
        }

        // Configurações fiscais
        if (entity.getInputTax() != null) {
            this.inputTax = new TaxConfigurationDTO(entity.getInputTax());
        }

        if (entity.getOutputTax() != null) {
            this.outputTax = new TaxConfigurationDTO(entity.getOutputTax());
        }

        // Dados de auditoria
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.createdBy = entity.getCreatedBy();
    }
}
