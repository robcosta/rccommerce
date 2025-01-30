package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @EntityGraph(attributePaths = {"roles", "permissions"})
    @Query("""
            SELECT c FROM Client c
            JOIN FETCH c.roles
            JOIN FETCH c.permissions
            WHERE (:id IS NULL OR c.id = :id)
            AND (UPPER(c.nameUnaccented) LIKE '%' || :name || '%')
            AND (UPPER(c.email)  LIKE '%' || :email || '%')
            AND (UPPER(c.cpf)  LIKE '%' || :cpf || '%')
            """)
    public Page<Client> searchAll(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("email") String email,
            @Param("cpf") String cpf,
            Pageable pageable);

    public Optional<Client> findByEmail(String email);
}
