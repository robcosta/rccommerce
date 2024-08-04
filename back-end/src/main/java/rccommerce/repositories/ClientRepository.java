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
			+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%'))")
	public Page<Client> searchByName(String name, Pageable pageable);
	
	public Optional<Client> findByEmail(String email);

	public Optional<Client> findByCpf(String cpf);
}

