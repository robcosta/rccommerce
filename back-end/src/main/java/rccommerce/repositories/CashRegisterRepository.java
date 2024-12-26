package rccommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.CashRegister;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    List<CashRegister> findByOperatorId(Long operatorId);
}
