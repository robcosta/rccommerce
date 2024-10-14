package rccommerce.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Operator;


@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

	@Query("SELECT obj FROM Operator obj "
			+ "JOIN FETCH obj.roles "
			+ "JOIN FETCH obj.permissions "
			+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%')) "
			+ "AND UPPER(obj.email) LIKE UPPER(CONCAT('%', :email,'%'))")
	public Page<Operator> searchAll(String name, String email, Pageable pageable);
}

