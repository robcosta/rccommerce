package rccommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.repositories.ClientRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.tests.FactoryUser;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {

	@InjectMocks
	private ClientService service;

	@Mock
	private ClientRepository repository;

	@Mock
	private MessageSource messageSource;

	private Long existsId, nonExistsId, dependentId;
	private Client client;
	private Pageable pageable;
	private ClientService serviceSpy;
	private ClientDTO dto;

	@BeforeEach
	void setUp() throws Exception {
		client = FactoryUser.createClient();
		dto = FactoryUser.createClientDTO(client);
		pageable = PageRequest.of(0, 10);
		existsId = 7L;
		nonExistsId = 100L;
		dependentId = 3L;

		when(repository.getReferenceById(existsId)).thenReturn(client);
		when(repository.saveAndFlush(any())).thenReturn(client);
		when(repository.existsById(existsId)).thenReturn(true);
		when(repository.existsById(dependentId)).thenReturn(true);
		when(repository.existsById(nonExistsId)).thenReturn(false);

		serviceSpy = Mockito.spy(service);
		doNothing().when(serviceSpy).copyDtoToEntity(any(), any());
		doNothing().when(serviceSpy).checkUserPermissions(any(), any(), any());
		when(messageSource.getMessage(any(String.class), any(), any())).thenReturn("Cliente");
	}

	@Test
	public void searchAllShouldReturnPagedClientMinDTO() {
		Example<Client> example = Example.of(client);

		// Mockando o repositório com o Example correto
		when(repository.findAll(eq(example), eq(pageable))).thenReturn(new PageImpl<>(List.of(client, client, client)));

		// Chamando o método do serviço com o Example real
		Page<ClientMinDTO> result = serviceSpy.searchAll(example, pageable);

		// Assertivas
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(3, result.getSize());
		Assertions.assertEquals(client.getName(), result.toList().get(0).getName());
		Assertions.assertEquals(client.getEmail(), result.toList().get(1).getEmail());
	}

	@Test
	public void searchAllShouldThrowResourceNotFoundExceptionWhenNonExixtsClient() {
		Example<Client> example = Example.of(client);

		when(repository.findAll(eq(example), eq(pageable))).thenReturn(Page.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.searchAll(example, pageable);
		});
	}

	@Test
	public void findByIdShouldReturnClientMinDTOWhenExistsId() {
		when(repository.findById(existsId)).thenReturn(Optional.of(client));

		ClientMinDTO result = serviceSpy.findById(existsId);

		Assertions.assertEquals(existsId, result.getId());
		Assertions.assertEquals(client.getName(), result.getName());
		Assertions.assertEquals(client.getEmail(), result.getEmail());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.findById(nonExistsId);
		});
	}

	@Test
	public void insertShouldReturnClientMinDTOWhenEmailIsUnique() {
		ClientMinDTO result = serviceSpy.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(client.getName(), result.getName());
		Assertions.assertEquals(client.getEmail(), result.getEmail());
	}

	@Test
	public void insertShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
		DataIntegrityViolationException e = new DataIntegrityViolationException("");
		e.toString().concat("EMAIL NULLS FIRST");
		Mockito.doThrow(e).when(repository).saveAndFlush(ArgumentMatchers.any());

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.insert(dto);
		});
	}

	@Test
	public void insertShouldTrowDatabaseExceptionWhenCpfDoesNotUnique() {
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.insert(dto);
		});
	}

	@Test
	public void updateShouldReturnClientMinDTOWhenExistsIdAndIdIsNot1AndEmailIsUnique() {
		ClientMinDTO result = serviceSpy.update(dto, existsId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsId, result.getId());
		Assertions.assertEquals(client.getName(), result.getName());
	}

	@Test
	public void updateShouldTrowEntityNotFoundExceptionWhenNonExistsClient() {
		Mockito.doThrow(EntityNotFoundException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.update(dto, nonExistsId);
		});
	}

	@Test
	public void updateShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
		DataIntegrityViolationException e = new DataIntegrityViolationException("");
		e.toString().concat("EMAIL NULLS FIRST");
		Mockito.doThrow(e).when(repository).saveAndFlush(ArgumentMatchers.any());

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.update(dto, existsId);
		});
	}

	@Test
	public void updateShouldTrowDatabaseExceptionWhenCpfDoesNotUnique() {
		doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.update(dto, existsId);
		});

	}

	@Test
	public void deleteShouldDoNothingWhenIdExistsAndIdDoesNotDependent() {
		doNothing().when(repository).deleteById(existsId);

		Assertions.assertDoesNotThrow(() -> {
			serviceSpy.delete(existsId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.delete(nonExistsId);
		});
	}

	@Test
	public void deleteShouldTrowDataIntegrityViolationExceptionWhenIdDependent() {
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.delete(dependentId);
		});
	}

	// @Test
	// public void copyDtoToEntityShouldCopyAllDataDtoForClient(){
	// service.copyDtoToEntity(dto, client);
	//
	// List<String> resultAuhts = client.getAuths().stream().map(x ->
	// x.getAuth()).toList();
	// List<String> resultRoles = client.getRoles().stream().map(x ->
	// x.getAuthority()).toList();
	//
	//
	// Assertions.assertEquals(dto.getName(), client.getName());
	// Assertions.assertEquals(dto.getEmail(), client.getEmail());
	// Assertions.assertEquals(dto.getCpf(), client.getCpf());
	// Assertions.assertTrue(resultAuhts.contains("READER"));
	// Assertions.assertTrue(resultRoles.contains("ROLE_CLIENT"));
	// }
	//
	// @Test
	// public void copyDtoToEntityShouldCopyAllDataDtoForClientWhenEmptyPassword(){
	// dto.getPassword().isEmpty();
	//
	// service.copyDtoToEntity(dto, client);
	//
	// List<String> resultAuhts = client.getAuths().stream().map(x ->
	// x.getAuth()).toList();
	// List<String> resultRoles = client.getRoles().stream().map(x ->
	// x.getAuthority()).toList();
	//
	//
	// Assertions.assertEquals(dto.getName(), client.getName());
	// Assertions.assertEquals(dto.getEmail(), client.getEmail());
	// Assertions.assertEquals(dto.getCpf(), client.getCpf());
	// Assertions.assertTrue(resultAuhts.contains("READER"));
	// Assertions.assertTrue(resultRoles.contains("ROLE_CLIENT"));
	// }
}
