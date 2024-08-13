package rccommerce.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.entities.Operator;
import rccommerce.tests.FactoryUser;

@DataJpaTest
public class OperatorRepositoryTests {
	
	@Autowired
	private OperatorRepository repository;
	
	private long existingId, nonExistingId;
	private String existsName, existsEmail;;
	private String nonExistsName, nonExistsEmail;
	private Operator operator;
	private long totalOperator;
	
	@BeforeEach
	void SetUp() throws Exception {
		existingId = 2L;
		nonExistingId = 100L;
		existsName = "Maria Brown";
		nonExistsName = "Richard";
		existsEmail = "maria@gmail.com";
		nonExistsEmail = "richard@gmail.com";
		operator = FactoryUser.createOperator();
		totalOperator = repository.count();
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		
		Optional<Operator> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());	
		Assertions.assertEquals(totalOperator - 1L, repository.count());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		operator.setId(null);
	
		Operator result = repository.save(operator);		
		
		Assertions.assertNotNull(result.getId());
		Assertions.assertEquals(totalOperator + 1L, repository.count());
	}
	
	@Test
	public void saveShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			operator.setId(null);
			operator.setEmail(existsEmail);
			repository.save(operator);			
		});
	}
	
	@Test
	public void updateShouldUpdateOperatorWhenIdExists() {
		operator.setId(existingId);
		operator.setName("Anthony");
		
		Operator result = repository.saveAndFlush(operator);
		
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals("Anthony", result.getName());
	}
	
	@Test
	public void updateShouldThrowEntityNotFoundExceptionWhenNonExistsId() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			Operator result = repository.getReferenceById(nonExistingId);
			result.setName("Paul");
			repository.saveAndFlush(result);
		});
	}
	
	@Test
	public void updateShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			Operator result = repository.getReferenceById(existingId);
			result.setEmail(existsEmail);
			repository.saveAndFlush(result);
		});
	}
	
	@Test
	public void findByIdShouldOptionalOperatorWhenExixtID() {
		Optional<Operator> result = repository.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.get().getId());
	}
	
	@Test
	public void findByIdShouldObjectEmptyWhenNonExixtId() {
		Optional<Operator> result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	
	@Test
	public void searchAllShouldReturnOperatorsWhenEmptyNameAndEmail() {
		Page<Operator> result = repository.searchAll("","", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(repository.count(), result.getContent().size());
	}
	
	@Test
	public void searchAllShouldReturnOperatorWhenExistsNameAndEmailIsEmpty() {
		Page<Operator> result = repository.searchAll(existsName,"", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsName, result.getContent().get(0).getName());
	}
	
	@Test
	public void searchAllShouldReturnOperatorWhenExistsEmailAndNameIsEmpty() {
		Page<Operator> result = repository.searchAll("",existsEmail, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
	}
	
	@Test
	public void searchAllShouldReturnOperatorWhenExistNameAndExistsEmail() {
		Page<Operator> result = repository.searchAll(existsName,existsEmail, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsName, result.getContent().get(0).getName());
		Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistNameAndEmailIsEmpty() {
		Page<Operator> result = repository.searchAll(nonExistsName, "", null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistEmailAndNameIsEmpty() {
		Page<Operator> result = repository.searchAll("", nonExistsName, null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistNameAndNonExistsEmail() {
		Page<Operator> result = repository.searchAll(nonExistsName, nonExistsEmail, null);
		
		Assertions.assertTrue(result.isEmpty());
	}

}
