package rccommerce.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.CashRegister;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    List<CashRegister> findByOperatorId(Long operatorId); // Caixas de operador específico

    @Query("""
        SELECT DISTINCT c 
        FROM CashRegister c
        JOIN FETCH c.operator o
        JOIN FETCH c.cashMovements cm
        JOIN FETCH cm.movementDetails md
        WHERE (:cashRegisterId IS NULL OR c.id = :cashRegisterId)
        AND (:operator IS NULL OR o.id = :operator)
        AND (:isOpen IS NULL OR (c.closeTime IS NULL AND :isOpen = TRUE) OR (c.closeTime IS NOT NULL AND :isOpen = FALSE))   
    """)
    Page<CashRegister> findCashRegister(
            @Param("cashRegisterId") Long cashRegisterId,
            @Param("operator") Long operator,
            @Param("isOpen") Boolean isOpen,
            // @Param("closeTimeStart") Instant closeTimeStart,
            // @Param("closeTimeEnd") Instant closeTimeEnd,
            Pageable pageable);

    // AND (:closeTimeStart IS NULL OR c.closeTime >= :closeTimeStart)
    // AND (:closeTimeEnd IS NULL OR c.closeTime <= :closeTimeEnd)
}
