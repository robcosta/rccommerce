package rccommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;

@DataJpaTest
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repository;
	
	private long existingId, nonExistingId;
	private String existsName, existsEmail;
	private String nonExistsName, nonExistsEmail;
	
	@BeforeEach
	void SetUp() throws Exception {
		existingId = 1L;
		nonExistingId = 100L;
		existsName = "Administrador";
		nonExistsName = "Richard";
		existsEmail = "admin@gmail.com";
		nonExistsEmail = "richard@gmail.com";
	}
	
	@Test
	public void findByIdShouldOptionalUserWhenExixtID() {
		Optional<User> result = repository.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.get().getId());
	}
	
	@Test
	public void findByIdShouldObjectEmptyWhenNonExixtId() {
		Optional<User> result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchUserAndRolesByEmailShouldLoggedUserWhenExistUser() {
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(existsEmail);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsEmail, result.get(0).getUsername());
	}
	
	@Test
	public void searchUserAndRolesByEmailShouObjectEmptyWhenNonExistUser() {
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(nonExistsEmail);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findByEmailShouldOptionalUserWhenExixtID() {
		Optional<User> result = repository.findByEmail(existsEmail);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsEmail, result.get().getEmail());
	}
	
	@Test
	public void findByEmailShouldObjectEmptyWhenNonExixtId() {
		Optional<User> result = repository.findByEmail(nonExistsEmail);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldReturnUsersWhenEmptyNameAndEmail() {
		Page<User> result = repository.searchAll("","", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(repository.count(), result.getContent().size());
	}
	
	@Test
	public void searchAllShouldReturnUserWhenExistsNameAndEmailIsEmpty() {
		Page<User> result = repository.searchAll(existsName,"", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsName, result.getContent().get(0).getName());
	}
	
	@Test
	public void searchAllShouldReturnUserWhenExistsEmailAndNameIsEmpty() {
		Page<User> result = repository.searchAll("",existsEmail, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
	}
	
	@Test
	public void searchAllShouldReturnUserWhenExistNameAndExistsEmail() {
		Page<User> result = repository.searchAll(existsName,existsEmail, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsName, result.getContent().get(0).getName());
		Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistNameAndEmailIsEmpty() {
		Page<User> result = repository.searchAll(nonExistsName, "", null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistEmailAndNameIsEmpty() {
		Page<User> result = repository.searchAll("", nonExistsName, null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistNameAndNonExistsEmail() {
		Page<User> result = repository.searchAll(nonExistsName, nonExistsEmail, null);
		
		Assertions.assertTrue(result.isEmpty());
	}
}
