package rccommerce.repositories;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.CashMovement;
import rccommerce.entities.enums.MovementType;

@Repository
public interface CashMovimentRepository extends JpaRepository<CashMovement, Long> {

    @Query(nativeQuery = true, value = """
            SELECT cm.payment_type, SUM(cm.amount)
            FROM tb_cash_movement cm 
            WHERE cm.cash_register_id = :cashRegisterId GROUP BY cm.payment_type
        """)
    Map<MovementType, BigDecimal> sumAmountsByPaymentType(Long cashRegisterId);
}
