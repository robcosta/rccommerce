package rccommerce.tests;

import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.ProductCategoryMinDTO;
import rccommerce.entities.ProductCategory;

public class FactoryCategory {

    public static ProductCategory createCategory() {
        ProductCategory category = new ProductCategory(7L, "BANHO");
        return category;
    }

    public static ProductCategory createNewCategory() {
        return new ProductCategory();
    }

    public static ProductCategoryDTO createCategoryDTO() {
        return new ProductCategoryDTO(createCategory());
    }

    public static ProductCategoryDTO createCategoryDTO(ProductCategory entity) {
        return new ProductCategoryDTO(entity);
    }

    public static ProductCategoryMinDTO createCategoryMinDTO() {
        return new ProductCategoryMinDTO(createCategory());
    }
}
