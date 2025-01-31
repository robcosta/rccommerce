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

    @EntityGraph(attributePaths = {"addresses", "roles", "permissions"})
    @Query("""
            SELECT c FROM Client c
            WHERE (:id IS NULL OR c.id = :id)
            AND (UPPER(c.nameUnaccented) LIKE '%' || UPPER(:name) || '%')
            AND (UPPER(c.email)  LIKE '%' || UPPER(:email) || '%')
            AND (UPPER(c.cpf)  LIKE '%' || UPPER(:cpf) || '%')
            """)
    public Page<Client> searchAll(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("email") String email,
            @Param("cpf") String cpf,
            Pageable pageable);

    @EntityGraph(attributePaths = {"addresses", "roles", "permissions"})
    @Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findByIdWithAddresses(@Param("id") Long id);

    public Optional<Client> findByEmail(String email);
}
