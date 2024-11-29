package rccommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rccommerce.entities.Category;

@Getter
@AllArgsConstructor
public class CategoryMinDTO {

    private Long id;
    private String name;

    public CategoryMinDTO(Category entity) {
        id = entity.getId();
        name = entity.getName();
    }
}
