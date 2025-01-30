package rccommerce.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rccommerce.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles", "permissions"})
    @Query("""
			SELECT u FROM User u 
			JOIN FETCH u.roles r 
			JOIN FETCH u.permissions p 
			WHERE u.email = :email
    """)
    public Optional<User> searchUserRolesAndPermissionsByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "permissions"})
    @Query("""
		SELECT obj FROM User obj 
		JOIN FETCH obj.roles 
		WHERE (:name IS NULL OR UPPER (obj.name) LIKE  '%'|| UPPER(:name) || '%')
		AND (:email IS NULL OR UPPER(obj.email) LIKE '%'|| UPPER(:email) || '%') 			
	""")
    public Page<User> searchAll(String name, String email, Pageable pageable);
}
