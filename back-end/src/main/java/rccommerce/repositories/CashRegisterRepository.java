package rccommerce.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.CashRegister;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    List<CashRegister> findByOperatorId(Long operatorId); // Caixas de operador específico

    // Banco Postgres
    @EntityGraph(attributePaths = {"operator", "cashMovements"})
    @Query("""
        SELECT c 
        FROM CashRegister c
        JOIN FETCH c.operator o
        JOIN FETCH c.cashMovements cm
        JOIN FETCH cm.movementDetails md
        WHERE (:cashRegisterId IS NULL OR c.id = :cashRegisterId)
        AND (:operator IS NULL OR o.id = :operator)
        AND (:isOpen IS NULL OR (c.closeTime IS NULL AND :isOpen = TRUE) OR (c.closeTime IS NOT NULL AND :isOpen = FALSE))   
        AND c.openTime >= COALESCE(CAST(:openTimeStart AS timestamp), '1900-01-01 00:00:00')   
        AND c.openTime <= COALESCE(CAST(:openTimeEnd AS timestamp), '2050-01-01 23:59:59') 
        """)
    Page<CashRegister> findCashRegister(
            @Param("cashRegisterId") Long cashRegisterId,
            @Param("operator") Long operator,
            @Param("isOpen") Boolean isOpen,
            @Param("openTimeStart") Instant openTimeStart,
            @Param("openTimeEnd") Instant openTimeEnd,
            Pageable pageable);

    // Banco H2        
    // @Query("""
    //     SELECT c 
    //     FROM CashRegister c
    //     JOIN FETCH c.operator o
    //     JOIN FETCH c.cashMovements cm
    //     JOIN FETCH cm.movementDetails md
    //     WHERE (:cashRegisterId IS NULL OR c.id = :cashRegisterId)
    //     AND (:operator IS NULL OR o.id = :operator)
    //     AND (:isOpen IS NULL OR (c.closeTime IS NULL AND :isOpen = TRUE) OR (c.closeTime IS NOT NULL AND :isOpen = FALSE))   
    //     AND (:openTimeStart IS NULL OR c.openTime >= CAST(:openTimeStart AS TIMESTAMP))
    //     AND (:openTimeEnd IS NULL OR c.openTime <= CAST(:openTimeEnd AS TIMESTAMP))
    //     """)
    // Page<CashRegister> findCashRegister(
    //         @Param("cashRegisterId") Long cashRegisterId,
    //         @Param("operator") Long operator,
    //         @Param("isOpen") Boolean isOpen,
    //         @Param("openTimeStart") Instant openTimeStart,
    //         @Param("openTimeEnd") Instant openTimeEnd,
    //         Pageable pageable);
    // Hibernate o MySql        
    // AND (:closeTimeStart IS NULL OR c.openTime >= CAST(:closeTimeStart AS TIMESTAMP))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= CAST(:closeTimeEnd AS TIMESTAMP))
    // PostgreSQL:
    // AND (:closeTimeStart IS NULL OR c.openTime >= DATE_TRUNC('second', :closeTimeStart))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= DATE_TRUNC('second', :closeTimeEnd))
}
