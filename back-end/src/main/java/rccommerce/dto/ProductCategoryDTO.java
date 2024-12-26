package rccommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.ProductCategory;

@AllArgsConstructor
@Getter
public class ProductCategoryDTO {

    private Long id;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
    private String name;

    public ProductCategoryDTO(ProductCategory entity) {
        id = entity.getId();
        name = entity.getName();
    }
}
