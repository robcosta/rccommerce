package rccommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entity.User;
import rccommerce.projections.UserDetailsProjection;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(nativeQuery = true, value = """
				SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
				FROM tb_user
				INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
				INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
				WHERE tb_user.email = :email
			""")
	List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
	
	Optional<User> findByEmail(String email);
}

//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//	@Query("""
//				SELECT DISTINCT obj.email AS userName,
//				 				obj.password AS password,
//								obj2.getId AS roleId,
//								obj.roles.getAuthority AS authority FROM User obj
//				JOIN FETCH obj.roles obj2
//				WHERE obj.email = : email
//			""")
//	List<UserDTO> searchUserAndRolesByEmail(String email);
//	
//	Optional<User> findByEmail(String email);
//}