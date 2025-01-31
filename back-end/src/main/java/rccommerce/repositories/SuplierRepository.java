package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Suplier;

@Repository
public interface SuplierRepository extends JpaRepository<Suplier, Long> {

    @EntityGraph(attributePaths = {"addresses"})
    @Query("""
            SELECT s FROM Suplier s
            WHERE (:id IS NULL OR s.id = :id)
            AND (UPPER(s.nameUnaccented) LIKE '%' || UPPER(:name) || '%')
            AND (UPPER(s.cnpj)  LIKE '%' || UPPER(:cnpj) || '%')
            """)
    public Page<Suplier> searchAll(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("cnpj") String cnpj,
            Pageable pageable);

    @EntityGraph(attributePaths = {"addresses"})
    @Query("SELECT s FROM Suplier s WHERE s.id = :id")
    Optional<Suplier> findByIdWithAddresses(@Param("id") Long id);

    Optional<Suplier> findByCnpj(String cnpj);
}
