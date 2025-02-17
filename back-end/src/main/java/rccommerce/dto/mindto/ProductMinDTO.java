package rccommerce.dto.mindto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import rccommerce.dto.ProductCategoryDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Getter
public class ProductMinDTO {

    private Long id;
    private String name;
    private String unit;
    private String description;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal price;
    private String imgUrl;
    // private BigDecimal qttStock;
    private String reference;
    // private SuplierMinDTO suplier;

    private List<ProductCategoryDTO> categories = new ArrayList<>();

    public ProductMinDTO(Long id, String name, String unit, BigDecimal price, String imgUrl,
            String reference, SuplierMinDTO suplier) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.imgUrl = imgUrl;
        this.reference = reference;
        // this.suplier = suplier;
    }

    public ProductMinDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        unit = entity.getUn();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        description = entity.getDescription();
        // qttStock = entity.getQuantity();
        reference = entity.getReference();
        // suplier = new SuplierMinDTO(entity.getSuplier());
        for (ProductCategory category : entity.getCategories()) {
            categories.add(new ProductCategoryDTO(category));
        }
    }
}
