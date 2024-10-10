package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Verify;


@Repository
public interface VerifyRepository extends JpaRepository<Verify, Long> {

	@Query("SELECT obj FROM Verify obj "
			+ "WHERE obj.very = :very")
	Verify findByVery(Integer very);
}

