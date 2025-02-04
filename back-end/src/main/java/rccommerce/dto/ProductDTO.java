package rccommerce.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Product;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
    private String name;

    private String description;

    @NotBlank(message = "Campo requerido")
    @Size(min = 2, max = 2, message = "Unidade com 2 caracteres.")
    private String unit;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @Positive(message = "Informe o preço do produto")
    private BigDecimal price;
    private String imgUrl;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @PositiveOrZero(message = "Informe a quantidade do produto, ou 0 se não houver estoque")
    private BigDecimal quantity;
    private String reference;
    private SuplierDTO suplier;

    @Size(min = 1, message = "Indique pelo menos uma categoria válida")
    private List<ProductCategoryDTO> categories = new ArrayList<>();

    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        unit = entity.getUnit();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        quantity = entity.getQuantity();
        reference = entity.getReference();
        suplier = new SuplierDTO(entity.getSuplier());
        entity.getCategories().forEach(category -> categories.add(new ProductCategoryDTO(category)));
    }
}
