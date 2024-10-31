package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Suplier;

@Repository
public interface SuplierRepository extends JpaRepository<Suplier, Long> {

    // @Query("SELECT obj FROM Suplier obj "
    // 		+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%')) "
    // 		+ "AND UPPER(obj.cnpj) LIKE UPPER(CONCAT('%', :cnpj,'%')) ")
    // public Page<Suplier> searchAll(String name, String cnpj, Pageable pageable);
    Optional<Suplier> findByCnpj(String cnpj);
}
