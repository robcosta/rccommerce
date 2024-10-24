package rccommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query(nativeQuery = true, value = """
	        SELECT tb_user.id As userId, tb_user.email AS username, tb_user.password, 
	               tb_role.id AS roleId, tb_role.authority, 
	               tb_permission.id AS permissionId, tb_permission.authority AS permissionAuthority
	        FROM tb_user
	        LEFT JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
	        LEFT JOIN tb_role ON tb_role.id = tb_user_role.role_id
	        LEFT JOIN tb_user_permission ON tb_user.id = tb_user_permission.user_id
	        LEFT JOIN tb_permission ON tb_permission.id = tb_user_permission.permission_id
	        WHERE tb_user.email = :email
	    """)
	public List<UserDetailsProjection> searchUserRolesAndPermissionsByEmail(String email);


//	@Query(nativeQuery = true, value = """
//				SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
//				FROM tb_user
//				INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
//				INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
//				WHERE tb_user.email = :email
//			""")
//	public List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
	
	public Optional<User> findByEmail(String email);
	
	@Query("SELECT u FROM User u "
	         + "LEFT JOIN FETCH u.roles r "
	         + "LEFT JOIN FETCH u.permissions p "
	         + "WHERE u.email = :email")
	public Optional<User> searchEmail(String email);
	
	@Query("SELECT obj FROM User obj "
			+ "JOIN FETCH obj.roles "
			+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%')) "
			+ "AND UPPER(obj.email) LIKE UPPER(CONCAT('%', :email,'%'))")
	public Page<User> searchAll(String name, String email, Pageable pageable);
}

