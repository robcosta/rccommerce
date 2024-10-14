package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Client;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query("SELECT obj FROM Client obj "
			+ "JOIN FETCH obj.roles "
			+ "JOIN FETCH obj.permissions "
			+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%')) "
			+ "AND UPPER(obj.email) LIKE UPPER(CONCAT('%', :email,'%')) "
			+ "AND UPPER(obj.cpf) LIKE UPPER(CONCAT('%', :cpf,'%')) ")
	public Page<Client> searchAll(String name, String email, String cpf, Pageable pageable);
	
	public Optional<Client> findByEmail(String email);
}

