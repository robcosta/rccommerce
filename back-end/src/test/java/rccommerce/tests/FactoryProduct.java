package rccommerce.tests;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import rccommerce.dto.ProductDTO;
import rccommerce.dto.mindto.ProductMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.entities.Suplier;

public class FactoryProduct {

    public static Product createProduct() {
        Product product = Product.builder()
                .id(26L)
                .name("Xiomi Poco X6 Pro 6G")
                .description("\"Lorem ipsum dolor sit amet")
                .un("UN")
                .price(new BigDecimal(2000.0))
                .imgUrl("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg")
                .quantity(new BigDecimal(3.0))
                .reference("7899381427567")
                .suplier(Suplier.builder()
                        .id(2L)
                        .name("Lojas FOO")
                        .cnpj("28104874000108")
                        .build())
                .categories(new HashSet<>(Set.of(new ProductCategory(1L, "LIVROS"), new ProductCategory(2L, "ELETRÔNICOS"))))
                .build();
        return product;
    }

    public static Product createNewProduct() {
        return new Product();
    }

    public static ProductDTO createProductDTO() {
        return new ProductDTO(createProduct());
    }

    public static ProductDTO createProductDTO(Product entity) {
        return new ProductDTO(entity);
    }

    public static ProductMinDTO createProductMinDTO() {
        return new ProductMinDTO(createProduct());
    }
}
