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
import rccommerce.entities.Client;
import rccommerce.tests.Factory;

@DataJpaTest
public class ClientRepositoryTests {
	
	@Autowired
	private ClientRepository repository;
	
	private long existingId, nonExistingId;
	private String existsName, existsEmail, existsCpf;
	private String nonExistsName, nonExistsEmail, nonExistsCPF;
	private Client client;
	private long totalClient;
	
	@BeforeEach
	void SetUp() throws Exception {
		existingId = 5L;
		nonExistingId = 100L;
		existsName = "John Red";
		nonExistsName = "Richard";
		existsEmail = "john@gmail.com";
		nonExistsEmail = "richard@gmail.com";
		existsCpf = "73995808042";
		nonExistsCPF = "00000000000";
		client = Factory.createClient();
		totalClient = repository.count();
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		
		Optional<Client> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());	
		Assertions.assertEquals(totalClient - 1L, repository.count());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		client.setId(null);
	
		Client result = repository.save(client);		
		
		Assertions.assertNotNull(result.getId());
		Assertions.assertEquals(totalClient + 1L, repository.count());
	}
	
	@Test
	public void saveShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			client.setId(null);
			client.setEmail(existsEmail);
			repository.save(client);			
		});
	}
	
	@Test
	public void updateShouldUpdateClientWhenIdExists() {
		client.setId(existingId);
		client.setName("Anthony");
		
		Client result = repository.saveAndFlush(client);
		
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals("Anthony", result.getName());
	}
	
	@Test
	public void updateShouldThrowEntityNotFoundExceptionWhenNonExistsId() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			Client result = repository.getReferenceById(nonExistingId);
			result.setName("Paul");
			repository.saveAndFlush(result);
		});
	}
	
	@Test
	public void updateShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			Client result = repository.getReferenceById(existingId);
			result.setEmail(existsEmail);
			repository.saveAndFlush(result);
		});
	}
	
	@Test
	public void findByIdShouldOptionalClientWhenExixtID() {
		Optional<Client> result = repository.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.get().getId());
	}
	
	@Test
	public void findByIdShouldObjectEmptyWhenNonExixtId() {
		Optional<Client> result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	
	@Test
	public void searchAllShouldReturnClientsWhenEmptyNameAndEmailAndCpf() {
		Page<Client> result = repository.searchAll("","","", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(repository.count(), result.getContent().size());
	}
	
	@Test
	public void searchAllShouldReturnClientWhenExistsNameAndEmptyEmailAndCpf() {
		Page<Client> result = repository.searchAll(existsName,"","", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsName, result.getContent().get(0).getName());
	}
	
	@Test
	public void searchAllShouldReturnClientWhenExistsEmailAndNameIsEmpty() {
		Page<Client> result = repository.searchAll("",existsEmail,"", null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
	}
	
	@Test
	public void searchAllShouldReturnClientWhenExistsCpfAndEmptyNameAndEmail() {
		Page<Client> result = repository.searchAll("","",existsCpf, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsCpf, result.getContent().get(0).getCpf());
	}
	
	@Test
	public void searchAllShouldReturnClientWhenExistNameAndEmailAndCpf() {
		Page<Client> result = repository.searchAll(existsName,existsEmail,existsCpf, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsName, result.getContent().get(0).getName());
		Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
		Assertions.assertEquals(existsCpf, result.getContent().get(0).getCpf());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistNameAndEmptyEmailAndCpf() {
		Page<Client> result = repository.searchAll(nonExistsName, "","", null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistEmailAndEmptyNameAndCpf() {
		Page<Client> result = repository.searchAll("", nonExistsName,"", null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistCpfAndEmptyNameAndEmail() {
		Page<Client> result = repository.searchAll("","",nonExistsCPF, null);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchAllShouldObjectEmptyWhenNonExistNameAndEmailAndCpf() {
		Page<Client> result = repository.searchAll(nonExistsName, nonExistsEmail,nonExistsCPF, null);
		
		Assertions.assertTrue(result.isEmpty());
	}

}
