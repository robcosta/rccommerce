package rccommerce.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Operator;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    @EntityGraph(attributePaths = {"roles", "permissions"})
    @Query("""
            SELECT o FROM Operator o
            JOIN FETCH o.roles
            JOIN FETCH o.permissions
            WHERE (:id IS NULL OR o.id = :id)
            AND (UPPER(o.nameUnaccented) LIKE '%' || UPPER(:name) || '%')
            AND (UPPER(o.email)  LIKE '%' || UPPER(:email) || '%')
           """)
    public Page<Operator> searchAll(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("email") String email,
            Pageable pageable);
}
