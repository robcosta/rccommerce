package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.CashRegister;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    @Query("SELECT obj FROM CashRegister obj "
            + "JOIN FETCH obj.operator "
            + "WHERE obj.operator.id = :operatorId ")
    public Optional<CashRegister> findByOperatorId(Long operatorId);
}
