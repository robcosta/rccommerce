package rccommerce.dto.fulldto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.mindto.SuplierMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.entities.Tax;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Getter
public class ProductFullDTO {

    private Long id;
    private String name;
    private String unit;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    private BigDecimal price;
    private String imgUrl;
    private BigDecimal qttStock;
    private String reference;
    private SuplierMinDTO suplier;

    private List<ProductCategoryDTO> categories = new ArrayList<>();

    private Tax inputTax;
    private Tax outputTax;

    public ProductFullDTO(Long id, String name, String unit, BigDecimal price, String imgUrl,
            String reference, SuplierMinDTO suplier, Tax inputTax, Tax outputTax) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.imgUrl = imgUrl;
        this.reference = reference;
        this.suplier = suplier;
        this.inputTax = inputTax;
        this.outputTax = outputTax;
    }

    public ProductFullDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        unit = entity.getUn();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        qttStock = entity.getQuantity();
        reference = entity.getReference();
        suplier = new SuplierMinDTO(entity.getSuplier());
        for (ProductCategory category : entity.getCategories()) {
            categories.add(new ProductCategoryDTO(category));
        }
        inputTax = entity.getInputTax();
        outputTax = entity.getOutputTax();
    }
}
