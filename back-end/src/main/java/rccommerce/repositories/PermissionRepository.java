package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Permission;


@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	@Query("SELECT obj FROM Permission obj "
			+ "WHERE UPPER(obj.authority) LIKE UPPER(CONCAT('%', :authority,'%'))")
	Permission findByAuthority(String authority);
}

