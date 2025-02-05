package rccommerce.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("""
        SELECT pc 
        FROM ProductCategory pc 
        WHERE (:id IS NULL OR pc.id = :id)
        AND (UPPER(pc.nameUnaccented) LIKE '%' || UPPER(:name) || '%')
        """)
    public Page<ProductCategory> searchAll(
            @Param("id") Long id,
            @Param("name") String name,
            Pageable pageable);
}
