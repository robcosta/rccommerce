package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Suplier;


@Repository
public interface SuplierRepository extends JpaRepository<Suplier, Long> {

	@Query("SELECT obj FROM Suplier obj "
			+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%'))")
	Suplier findBySuplier(String name);

}

