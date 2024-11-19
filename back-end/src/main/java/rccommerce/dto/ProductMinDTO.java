package rccommerce.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import rccommerce.entities.Category;
import rccommerce.entities.Product;

public class ProductMinDTO {

    private Long id;
    private String name;
    private String unit;
    private BigDecimal price;
    private String imgUrl;
    private BigDecimal qttStock;
    private String reference;
    private SuplierMinDTO suplier;

    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductMinDTO(Long id, String name, String unit, BigDecimal price, String imgUrl,
            String reference, SuplierMinDTO suplier) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.imgUrl = imgUrl;
        this.reference = reference;
        this.suplier = suplier;
    }

    public ProductMinDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        unit = entity.getUnit();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        reference = entity.getReference();
        suplier = new SuplierMinDTO(entity.getSuplier());
        for (Category category : entity.getCategories()) {
            categories.add(new CategoryDTO(category));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public BigDecimal getQttStock() {
        return qttStock;
    }

    public String getReference() {
        return reference;
    }

    public SuplierMinDTO getSuplier() {
        return suplier;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
