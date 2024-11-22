package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.CashMovement;

@Repository
public interface CashMovimentRepository extends JpaRepository<CashMovement, Long> {

}
