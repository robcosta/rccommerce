package rccommerce.repositories;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // @Query("""
    //     SELECT o
    //     FROM Order o                
    //     WHERE (:id IS NULL OR o.id = :id)
    //     AND (:timeStart IS NULL OR o.moment >= CAST(:timeStart AS TIMESTAMP))
    //     AND (:timeEnd IS NULL OR o.moment <= CAST(:timeEnd AS TIMESTAMP))
    //     AND (:status IS NULL OR UPPER(o.status) LIKE UPPER(CONCAT('%', :status,'%'))) 
    //     AND EXISTS(
    //         SELECT 1 FROM o.user u
    //         WHERE (:userid IS NULL OR u.id = :userid)
    //         AND (:username IS NULL OR UPPER(u.nameUnaccented) LIKE UPPER(CONCAT('%', :username,'%')))
    //     )
    //     AND EXISTS(
    //         SELECT 1 FROM o.client c 
    //         WHERE(:clientid IS NULL OR c.id = :clientid)
    //         AND (:clientname IS NULL OR UPPER(c.nameUnaccented) LIKE UPPER(CONCAT('%', :clientname,'%')))
    //     )       
    //     """)
    @EntityGraph(attributePaths = {"user", "client", "itens"})
    @Query("""
        SELECT DISTINCT o
        FROM Order o       
        JOIN FETCH o.user u
        JOIN FETCH o.client c
        LEFT JOIN FETCH o.payment p                        
        WHERE (:id IS NULL OR o.id = :id)
        AND (:userid IS NULL OR u.id = :userid)
        AND (:username IS NULL OR UPPER(u.nameUnaccented) LIKE UPPER(CONCAT('%', :username,'%')))
        AND (:clientid IS NULL OR c.id = :clientid)
        AND (:clientname IS NULL OR UPPER(c.nameUnaccented) LIKE UPPER(CONCAT('%', :clientname,'%')))
        AND (:status IS NULL OR UPPER(o.status) LIKE UPPER(CONCAT('%', :status,'%')))   
        AND (:timeStart IS NULL OR o.moment >= CAST(:timeStart AS TIMESTAMP))
        AND (:timeEnd IS NULL OR o.moment <= CAST(:timeEnd AS TIMESTAMP))
        """)
    Page<Order> findOrder(
            @Param("id") Long id,
            @Param("userid") Long userid,
            @Param("username") String username,
            @Param("clientid") Long clientid,
            @Param("clientname") String clientname,
            @Param("status") String status,
            @Param("timeStart") Instant timeStart,
            @Param("timeEnd") Instant timeEnd,
            Pageable pageable);

    // Hibernate o MySql        
    // AND (:closeTimeStart IS NULL OR c.openTime >= CAST(:closeTimeStart AS TIMESTAMP))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= CAST(:closeTimeEnd AS TIMESTAMP))
    // PostgreSQL:
    // AND (:closeTimeStart IS NULL OR c.openTime >= DATE_TRUNC('second', :closeTimeStart))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= DATE_TRUNC('second', :closeTimeEnd))
}
