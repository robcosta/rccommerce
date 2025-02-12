package rccommerce.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    private TaxDTO inputTax;
    private TaxDTO outputTax;

    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        unit = entity.getUn();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        quantity = entity.getQuantity();
        reference = entity.getReference();
        suplier = new SuplierMinDTO(entity.getSuplier());
        for (ProductCategory category : entity.getCategories()) {
            categories.add(new ProductCategoryDTO(category));
        }
        inputTax = entity.getInputTax() != null ? new TaxDTO(entity.getInputTax()) : null;
        outputTax = entity.getOutputTax() != null ? new TaxDTO(entity.getOutputTax()) : null;
    }
}
