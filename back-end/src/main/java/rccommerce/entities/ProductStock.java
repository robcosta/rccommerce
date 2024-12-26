package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.ProductStockDTO;
import rccommerce.dto.ProductStockMinDTO;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.services.interfaces.Convertible;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_Product_stock")
public class ProductStock implements Convertible<ProductStockDTO, ProductStockMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(precision = 15, scale = 2)
    private BigDecimal quantity;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;

    @Column(precision = 15, scale = 2)
    private BigDecimal qttMoved;

    private StockMoviment moviment;

    public ProductStock(Long id, User user, Product product, BigDecimal quantity, Instant moment, BigDecimal qttMoved) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.moment = moment;
        this.qttMoved = qttMoved;
    }

    public void setQttMoved(BigDecimal qttMoved) {
        this.qttMoved = qttMoved;
        switch (this.moviment) {
            case BUY, INPUT ->
                this.quantity = this.quantity.add(qttMoved);
            case SALE, OUTPUT, TRANSFER ->
                this.quantity = this.quantity.subtract(qttMoved);
        }
    }

    @Override
    public ProductStockDTO convertDTO() {
        return new ProductStockDTO(this);
    }

    @Override
    public ProductStockMinDTO convertMinDTO() {
        return new ProductStockMinDTO(this);
    }
}
