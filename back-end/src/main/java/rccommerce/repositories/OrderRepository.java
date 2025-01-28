package rccommerce.repositories;

import java.time.Instant;
import java.util.Optional;

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

    @SuppressWarnings("null")
    @Override
    @EntityGraph(attributePaths = {"user", "client"})
    @Query("""
        SELECT o
        FROM Order o
        LEFT JOIN FETCH o.user u
        LEFT JOIN FETCH o.client c
        LEFT JOIN FETCH o.payment p
        LEFT JOIN FETCH o.itens i
        LEFT JOIN FETCH i.id.product prod
        WHERE (:id IS NULL OR o.id = :id)        
    """)
    Optional<Order> findById(@Param("id") Long id);

    // Banco H2
    @EntityGraph(attributePaths = {"user", "client"})
    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.user u
        JOIN FETCH o.client c
        LEFT JOIN FETCH o.payment p
        LEFT JOIN FETCH o.itens i
        LEFT JOIN FETCH i.id.product prod
        WHERE (:id IS NULL OR o.id = :id)
        AND(UPPER(o.status) LIKE '%' || :status || '%')
        AND (:userId IS NULL OR u.id = :userId)
        AND (UPPER(u.nameUnaccented) LIKE '%' || :userName || '%')
        AND (:clientId IS NULL OR c.id = :clientId)
        AND (UPPER(c.nameUnaccented) LIKE '%' || :clientName || '%')
        AND o.moment >= COALESCE(CAST(:timeStart AS timestamp), '1900-01-01 00:00:00')   
        AND o.moment <= COALESCE(CAST(:timeEnd AS timestamp), '2050-01-01 23:59:59')   
    """)
    Page<Order> findOrder(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("userName") String userName,
            @Param("clientId") Long clientId,
            @Param("clientName") String clientName,
            @Param("timeStart") Instant timeStart,
            @Param("timeEnd") Instant timeEnd,
            Pageable pageable);

//     @Query("""
//     SELECT o
//     FROM Order o
//     JOIN FETCH o.user u
//     JOIN FETCH o.client c
//     LEFT JOIN FETCH o.payment p
//     LEFT JOIN FETCH o.itens i
//     LEFT JOIN FETCH i.id.product prod
//     WHERE (:id IS NULL OR o.id = :id)
//     AND o.moment >= CAST('2023-01-01T00:00:00Z' AS timestamp)
// """)
//     Page<Order> findOrder2(
//             @Param("id") Long id,
//             Pageable pageable);
    // @EntityGraph(attributePaths = {"user", "client"})
    // @Query("""
    //     SELECT o
    //     FROM Order o
    //     LEFT JOIN FETCH o.user u
    //     LEFT JOIN FETCH o.client c
    //     LEFT JOIN FETCH o.payment p
    //     LEFT JOIN FETCH o.itens i
    //     LEFT JOIN FETCH i.id.product prod
    //     WHERE (:id IS NULL OR o.id = :id)
    //     AND (:userid IS NULL OR u.id = :userid)
    //     AND (:username IS NULL OR UPPER(u.nameUnaccented) LIKE UPPER(CONCAT('%', :username,'%')))
    //     AND (:clientid IS NULL OR c.id = :clientid)
    //     AND (:clientname IS NULL OR UPPER(c.nameUnaccented) LIKE UPPER(CONCAT('%', :clientname,'%')))
    //     AND (:status IS NULL OR UPPER(o.status) LIKE UPPER(CONCAT('%', :status,'%')))
    //     AND (:timeStart IS NULL OR o.moment >= CAST(:timeStart AS TIMESTAMP))
    //     AND (:timeEnd IS NULL OR o.moment <= CAST(:timeEnd AS TIMESTAMP))
    // """)
    // Page<Order> findOrder(
    //         @Param("id") Long id,
    //         @Param("userid") Long userid,
    //         @Param("username") String username,
    //         @Param("clientid") Long clientid,
    //         @Param("clientname") String clientname,
    //         @Param("status") String status,
    //         @Param("timeStart") Instant timeStart,
    //         @Param("timeEnd") Instant timeEnd,
    //         Pageable pageable);
    // Banco Postgres
    // @EntityGraph(attributePaths = {"user", "client"})
    // @Query("""
    //     SELECT o
    //     FROM Order o
    //     LEFT JOIN FETCH o.user u
    //     LEFT JOIN FETCH o.client c
    //     LEFT JOIN FETCH o.payment p
    //     LEFT JOIN FETCH o.itens i
    //     LEFT JOIN FETCH i.id.product prod
    //     WHERE (:id IS NULL OR o.id = :id)
    //     AND (:userid IS NULL OR u.id = :userid)
    //     AND (:username IS NULL OR UPPER(u.nameUnaccented) LIKE UPPER(CONCAT('%', :username,'%')))
    //     AND (:clientid IS NULL OR c.id = :clientid)
    //     AND (:clientname IS NULL OR UPPER(c.nameUnaccented) LIKE UPPER(CONCAT('%', :clientname,'%')))
    //     AND (:status IS NULL OR UPPER(o.status) LIKE UPPER(CONCAT('%', :status,'%')))
    //     AND (:timeStart IS NULL OR c.openTime >= DATE_TRUNC('second', :timeStart))
    //     AND (:timeEnd IS NULL OR c.openTime <= DATE_TRUNC('second', :timeEnd))
    // """)
    // Page<Order> findOrder(
    //         @Param("id") Long id,
    //         @Param("userid") Long userid,
    //         @Param("username") String username,
    //         @Param("clientid") Long clientid,
    //         @Param("clientname") String clientname,
    //         @Param("status") String status,
    //         @Param("timeStart") Instant timeStart,
    //         @Param("timeEnd") Instant timeEnd,
    //         Pageable pageable);
    // Hibernate o MySql        
    // AND (:closeTimeStart IS NULL OR c.openTime >= CAST(:closeTimeStart AS TIMESTAMP))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= CAST(:closeTimeEnd AS TIMESTAMP))
    // PostgreSQL:
    // AND (:closeTimeStart IS NULL OR c.openTime >= DATE_TRUNC('second', :closeTimeStart))
    // AND (:closeTimeEnd IS NULL OR c.openTime <= DATE_TRUNC('second', :closeTimeEnd))
}
