package rccommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.Role;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.InvalidPasswordExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.tests.FactoryUser;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {

	@InjectMocks
	private ClientService service;

	@Mock
	private ClientRepository repository;
	
	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserService userService;

	private String existsName, nonExistsName, emptyName;
	private String existsEmail, nonExistsEmail,emptyEmail;
	private String existsCpf, nonExistsCpf, formatedCpf, emptyCpf;
	private long existingId, nonExistingId, integrityViolationId;
	private Client client;
	Pageable pageable;	
	private ClientService serviceSpy;
	private ClientDTO dto;
	private Role role;

	@BeforeEach
	void setUp() throws Exception {
		client = FactoryUser.createClient();
		dto = FactoryUser.createClientDTO(client);
		pageable = PageRequest.of(0, 10);
		existsName = client.getName();
		nonExistsName = "Other Client";
		existsEmail = client.getEmail();
		nonExistsEmail = "bar@gmail.com";
		emptyEmail = "";
		existsCpf = "59395734019";
		nonExistsCpf = "96191581050";
		formatedCpf = "593.957.340-19";
		emptyCpf = "";
		existingId = client.getId();
		nonExistingId = 100L;
		integrityViolationId = 2L;
		emptyName = "";
		role = FactoryUser.createRoleClient();
		
//		serviceSpy = Mockito.spy(service);
//		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, client);
		
//		Mockito.doNothing().when(userService).copyDtoToEntity(dto, client);
		
		Mockito.when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(role);
	}

	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenEmptyParams() {
		Mockito.when(repository.searchAll(emptyName, emptyEmail, emptyCpf, pageable))
				.thenReturn(new PageImpl<>(List.of(client, client)));

		Page<ClientMinDTO> result = service.findAll(emptyName, emptyEmail, emptyCpf, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 2);
		Assertions.assertEquals(result.toList().get(1).getName(), existsName);
		Assertions.assertEquals(result.toList().get(1).getEmail(), existsEmail);
		Assertions.assertEquals(result.toList().get(0).getCpf(), formatedCpf);
	}
	
	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenExistingName() {
		Mockito.when(repository.searchAll(existsName, emptyEmail, emptyCpf, pageable))
		.thenReturn(new PageImpl<>(List.of(client)));
		
		Page<ClientMinDTO> result = service.findAll(existsName, emptyEmail, emptyCpf, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.toList().get(0).getName(), existsName);
		Assertions.assertEquals(result.toList().get(0).getEmail(), existsEmail);
		Assertions.assertEquals(result.toList().get(0).getCpf(), formatedCpf);
	}
	
	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenExistingEmail() {
		Mockito.when(repository.searchAll(emptyName, existsEmail, emptyCpf, pageable))
		.thenReturn(new PageImpl<>(List.of(client)));
		
		Page<ClientMinDTO> result = service.findAll(emptyName, existsEmail, emptyCpf, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.toList().get(0).getName(), existsName);
		Assertions.assertEquals(result.toList().get(0).getEmail(), existsEmail);
		Assertions.assertEquals(result.toList().get(0).getCpf(), formatedCpf);
	}

	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenExistingCpf() {
		Mockito.when(repository.searchAll(emptyName, emptyEmail, existsCpf, pageable))
		.thenReturn(new PageImpl<>(List.of(client)));
		
		Page<ClientMinDTO> result = service.findAll(emptyName, emptyEmail, existsCpf, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.toList().get(0).getName(), existsName);
		Assertions.assertEquals(result.toList().get(0).getEmail(), existsEmail);
		Assertions.assertEquals(result.toList().get(0).getCpf(), formatedCpf);
	}
	
	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenExistsNameAndEmailAndCpf() {
		Mockito.when(repository.searchAll(existsName, existsEmail, existsCpf, pageable))
		.thenReturn(new PageImpl<>(List.of(client)));
		
		Page<ClientMinDTO> result = service.findAll(existsName, existsEmail, existsCpf, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.toList().get(0).getName(), existsName);
		Assertions.assertEquals(result.toList().get(0).getEmail(), existsEmail);
		Assertions.assertEquals(result.toList().get(0).getCpf(), formatedCpf);
	}

	@Test
	public void findAllShouldfindAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsName() {
		Mockito.when(repository.searchAll(nonExistsName,emptyEmail, emptyCpf , pageable)).thenReturn(new PageImpl<>(List.of()));

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistsName,emptyEmail, emptyCpf, pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsEmail() {
		Mockito.when(repository.searchAll(emptyName,nonExistsEmail, emptyCpf, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(emptyName,nonExistsEmail, emptyCpf, pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsCpf() {
		Mockito.when(repository.searchAll(emptyName,emptyEmail, nonExistsCpf, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(emptyName,emptyEmail, nonExistsCpf, pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsNameAndEmailAndCpf() {
		Mockito.when(repository.searchAll(nonExistsName,nonExistsEmail,nonExistsCpf, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistsName, nonExistsEmail, nonExistsCpf, pageable);
		});
	}

	@Test
	public void findByIdShouldReturnClientMinDTOWhenExistsId() {
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));

		ClientMinDTO result = service.findById(existingId);

		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), client.getName());
		Assertions.assertEquals(result.getEmail(), client.getEmail());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void insertShouldReturnClientMinDTOWhenEmailIsUnique() {
		client.setId(null);

		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(client);

		ClientMinDTO result = service.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), client.getName());
		Assertions.assertEquals(result.getEmail(), client.getEmail());
	}

	@Test
	public void insertShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
		client.setId(null);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(client);

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.insert(dto);
		});
	}

	@Test
	public void updateShouldReturnClientMinDTOWhenExistsId() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(client);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(client);

		ClientMinDTO result = service.update(dto, existingId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), client.getName());
	}
	
	@Test
	public void updateShouldReturnClientMinDTOWhenExistsIdAndEmptyPassword() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(client);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(client);
		client.setPassword("");
		dto = FactoryUser.createClientDTO(client);
		
		ClientMinDTO result = service.update(dto, existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), client.getName());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(dto, nonExistingId);
		});

	}

	@Test
	public void updateShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(client);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(client);

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.update(dto, existingId);
		});
	}

//	@Test
//	public void updateShouldVerifiPaswordWhenPassswordNotEmpty() {
//		Mockito.when(repository.getReferenceById(existingId)).thenReturn(client);
//		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(client);
//
//		ClientMinDTO result = service.update(dto, existingId);
//
//		Assertions.assertNotNull(result);
//		Assertions.assertEquals(result.getId(), existingId);
//		Assertions.assertEquals(result.getName(), client.getName());
//	}

	@Test
	public void deleteShouldDoNothingWhenExixstsId() {
		Client clientLogged = client;
		clientLogged.setId(20L);
		Mockito.doReturn(clientLogged).when(userService).authenticated();
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.doNothing().when(repository).deleteById(existingId);

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

	}

//	@Test
//	public void deleteShoulThronForbiddenExceptionWhenTryToDeleteLoggedInClient() {
//		Mockito.when(repository.existsById(existingId)).thenReturn(true);
//		Mockito.doReturn(client).when(userService).authenticated();
//
//		Assertions.assertThrows(ForbiddenException.class, () -> {
//			service.delete(existingId);
//		});
//	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenBreachOfIntegrity() {
		Mockito.when(repository.existsById(integrityViolationId)).thenReturn(true);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(integrityViolationId);
		Mockito.doReturn(client).when(userService).authenticated();

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(integrityViolationId);
		});
	}

//	@Test
//	public void checkPassordShouldThrowInvalidPasswordExecptionWhenPasswordLessThanFuorCaracters() {
//		String password = "123";
//
//		Assertions.assertThrows(InvalidPasswordExecption.class, () -> {
//			service.checkPassword(password);
//		});
//	}
//	
//	public void checkPassordShouldThrowInvalidPasswordExecptionWhenPasswordGreaterThanEighCaracters() {
//		String password = "123456789";
//
//		Assertions.assertThrows(InvalidPasswordExecption.class, () -> {
//			service.checkPassword(password);
//		});
//	}
//	
//	@Test
//	public void checkPassordShouldThrowInvalidPasswordExecptionWhenPasswordPasswordIsNotPositiveInteger() {
//		String password = "A345-4";
//		
//		Assertions.assertThrows(InvalidPasswordExecption.class, () -> {
//			service.checkPassword(password);
//		});
//	}
//
//	@Test
//	public void checkPassordShouldTrueWithPasswordIsOk() {
//		String password = "123456";
//
//		boolean result = serviceSpy.checkPassword(password);
//
//		Assertions.assertTrue(result);
//	}
}
