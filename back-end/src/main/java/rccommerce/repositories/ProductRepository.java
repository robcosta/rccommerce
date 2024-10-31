package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // @Query("SELECT obj FROM Product obj "
    // 		+ "JOIN FETCH obj.categories "
    // 		+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%')) "
    // 		+ "AND UPPER(obj.reference) LIKE UPPER(CONCAT('%', :reference,'%'))")
    // public Page<Product> searchAll(String name, String reference, Pageable pageable);
    Optional<Product> findByReference(String codeBarra);

}
