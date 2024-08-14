package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Auth;


@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

	@Query("SELECT obj FROM Auth obj "
			+ "WHERE UPPER(obj.auth) LIKE UPPER(CONCAT('%', :auth,'%'))")
	Auth findByAuth(String auth);

}

