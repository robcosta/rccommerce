package rccommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.ProductCategory;

@Getter
@AllArgsConstructor
public class ProductCategoryMinDTO {

    private Long id;
    private String name;

    public ProductCategoryMinDTO(ProductCategory entity) {
        id = entity.getId();
        name = entity.getName();
    }
}
