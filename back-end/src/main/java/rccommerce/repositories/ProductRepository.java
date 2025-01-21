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
        SELECT p 
        FROM Product p
        JOIN FETCH p.suplier s            
        JOIN FETCH p.categories c            
        WHERE (:id IS NULL OR p.id = :id)
        AND (:name IS NULL OR UPPER(p.nameUnaccented) LIKE UPPER(CONCAT('%', :name,'%')))
        AND (:reference IS NULL OR UPPER(p.reference) LIKE UPPER(CONCAT('%', :reference,'%')))
        AND (:suplierId IS NULL OR s.id = :suplierId)
        AND (:categoryId IS NULL OR c.id = :categoryId)        
        """)
    Page<Product> findProduct(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("reference") String reference,
            @Param("suplierId") Long suplierId,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    // Hibernate o MySql        
    // AND (:closeTimeStart IS NULL OR c.openTime >= CAST(:closeTimeStart AS TIMESTAMP))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= CAST(:closeTimeEnd AS TIMESTAMP))
    // PostgreSQL:
    // AND (:closeTimeStart IS NULL OR c.openTime >= DATE_TRUNC('second', :closeTimeStart))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= DATE_TRUNC('second', :closeTimeEnd))
    // 4. Otimizações Avançadas
    //  A. Full-Text Search (Busca Textual Completa)
    //  Se buscas por nomes são frequentes e precisam de flexibilidade:
    //  No PostgreSQL, use índices GIN para buscas textuais com extensões como pg_trgm.
    //  Exemplo de sql
    //      CREATE INDEX idx_product_name_trgm ON tb_product USING gin (nameUnaccented gin_trgm_ops);
    //  B. Cache para Consultas Frequentes
    //  Se algumas consultas são executadas repetidamente, implemente um cache na aplicação para 
    //  reduzir a carga no banco.
}
