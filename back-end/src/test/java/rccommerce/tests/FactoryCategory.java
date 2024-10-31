package rccommerce.tests;

import rccommerce.dto.CategoryDTO;
import rccommerce.dto.CategoryMinDTO;
import rccommerce.entities.Category;

public class FactoryCategory {

    public static Category createCategory() {
        Category category = new Category(7L, "BANHO");
        return category;
    }

    public static Category createNewCategory() {
        return new Category();
    }

    public static CategoryDTO createCategoryDTO() {
        return new CategoryDTO(createCategory());
    }

    public static CategoryDTO createCategoryDTO(Category entity) {
        return new CategoryDTO(entity);
    }

    public static CategoryMinDTO createCategoryMinDTO() {
        return new CategoryMinDTO(createCategory());
    }
}
