package rccommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entity.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query("SELECT obj FROM Role obj "
			+ "WHERE UPPER(obj.authority) LIKE UPPER(CONCAT('%', :authority,'%'))")
	Role findByAuthority(String authority);

}

