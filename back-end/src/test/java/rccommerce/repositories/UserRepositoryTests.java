package rccommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.tests.Factory;

@DataJpaTest
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalUser;
	private String existsName;
	private String nonExistsName;
	private String existsEmail;
	private String nonExistsEmail;
	private User user;
	
	
	
	@BeforeEach
	void SetUp() throws Exception {
		existingId = 2L;
		nonExistingId = 100L;
		countTotalUser = repository.count();
		existsName = "Maria";
		nonExistsName = "Richard";
		existsEmail = "maria@gmail.com";
		nonExistsEmail = "richard@gmail.com";
		user = Factory.createUser();
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		Optional<User> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());		
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		user.setId(null);
		user = repository.save(user);
		
		Assertions.assertNotNull(user.getId());
		Assertions.assertEquals(countTotalUser + 1, user.getId());
	}
	
	@Test
	public void saveShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
		
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			user.setId(null);
			user.setEmail(existsEmail);
			repository.save(user);			
		});
	}
	
	@Test
	public void updateShouldUpdateUserWhenIdExists() {
		user.setId(existingId);
		user.setName("Anthony");
		user = repository.saveAndFlush(user);
		
		Assertions.assertEquals(existingId, user.getId());
		Assertions.assertEquals("Anthony", user.getName());
	}
	
	@Test
	public void updateShouldThrowEntityNotFoundExceptionWhenNonExistsId() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			User result = repository.getReferenceById(nonExistingId);
			result.setName("Paul");
			repository.saveAndFlush(result);
		});
	}
	
	@Test
	public void updateShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			User result = repository.getReferenceById(existingId);
			result.setEmail(existsEmail);
			repository.saveAndFlush(result);
		});
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
	public void searchByNameShouldReturnUserWhenExistName() {
		Page<User> result = repository.searchByName(existsName, null);
		
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void searchByNameShouldObjectEmptyWhenNonExistName() {
		Page<User> result = repository.searchByName(nonExistsName, null);
		
		Assertions.assertTrue(result.isEmpty());
	}

}
