package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        SELECT DISTINCT p 
        FROM product p
        JOIN FETCH p.suplier s
        JOIN FETCH p.categories c       
        WHERE (:id IS NULL OR p.id = :id)
        AND (:name IS NULL OR UPPER(p.name) LIKE UPPER(CONCAT('%', :name,'%')))
        AND (:reference IS NULL OR UPPER(p.reference) LIKE UPPER(CONCAT('%', :reference,'%')))
        AND (:suplier IS NULL OR UPPER(s.name) LIKE UPPER(CONCAT('%', :suplier,'%')))
        AND (:suplier IS NULL OR UPPER(c.name) LIKE UPPER(CONCAT('%', :category,'%')))
        """)
    Page<Product> findProduct(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("reference") String reference,
            @Param("suplier") String suplier,
            @Param("category") String category,
            Pageable pageable);

}
