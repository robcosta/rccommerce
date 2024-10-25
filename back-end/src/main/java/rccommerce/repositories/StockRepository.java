package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // @Query("SELECT MAX(obj2.id) FROM Stock obj2 WHERE obj2.product.id =
    // :productId ")
    // public Stock searcLastMovement(Long productId);

    // @Query("SELECT obj FROM Stock obj "
    // + "WHERE obj.id = (SELECT MAX(obj2.id) FROM Stock obj2 WHERE obj2.product.id
    // = :productId) ")
    // public Stock searcLastMovement(Long productId);
}

// SELECIONA A ULTIMA MOVIMENTACAO DO ESTOQUE DE DETERMINADO PRODUTO
// SELECT * FROM TB_STOCK WHERE ID = (SELECT MAX(ID) FROM TB_STOCK WHERE
// PRODUCT_ID = 3)