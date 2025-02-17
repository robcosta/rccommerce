package rccommerce.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.dto.mindto.SuplierMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@AllArgsConstructor
@Getter
public class ProductDTO {

    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 3, max = 80, message = "O nome deve ter entre 3 e 80 caracteres")
    private String name;

    private String description;

    @NotBlank(message = "A unidade de medida é obrigatória")
    @Size(min = 2, max = 2, message = "A unidade deve ter exatamente 2 caracteres")
    private String unit;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal price;
    
    private String imgUrl;
    private BigDecimal quantity;
    private String reference;
    private SuplierMinDTO suplier;

    @Size(min = 1, message = "Selecione pelo menos uma categoria")
    private List<ProductCategoryDTO> categories = new ArrayList<>();

    @Valid
    private TaxConfigurationDTO inputTax;

    @Valid
    private TaxConfigurationDTO outputTax;

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.unit = entity.getUn();
        this.price = entity.getPrice();
        this.imgUrl = entity.getImgUrl();
        this.quantity = entity.getQuantity();
        this.reference = entity.getReference();
        this.suplier = new SuplierMinDTO(entity.getSuplier());
        
        for (ProductCategory category : entity.getCategories()) {
            categories.add(new ProductCategoryDTO(category));
        }

        if (entity.getInputTax() != null) {
            this.inputTax = new TaxConfigurationDTO(entity.getInputTax());
        }

        if (entity.getOutputTax() != null) {
            this.outputTax = new TaxConfigurationDTO(entity.getOutputTax());
        }
    }
}
