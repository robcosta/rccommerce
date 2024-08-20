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
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Auth;
import rccommerce.entities.Client;
import rccommerce.entities.Role;
import rccommerce.repositories.AuthRepository;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.Authentication;
import rccommerce.tests.FactoryUser;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {

	@InjectMocks
	private ClientService service;

	@Mock
	private ClientRepository repository;
	
	@Mock
	private AuthRepository authRepository;
	
	@Mock
	private Authentication authentication; 
	
	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserService userService;

	private String existsNameClient, nonExistsNameClient, emptyNameClient;
	private String existsEmail, nonExistsEmail, emptyEmail;
	private String existsCpf, nonExistsCpf, emptyCpf;
	private long existsId, nonExistsId, dependentId;
	private Client client;
	Pageable pageable;	
	private ClientService serviceSpy;
	private ClientDTO dto;
	private UserMinDTO userMinDTO;
	

	@BeforeEach
	void setUp() throws Exception {
		client = FactoryUser.createClient();
		dto = FactoryUser.createClientDTO(client);
		userMinDTO = FactoryUser.createUserMinDTO();
		pageable = PageRequest.of(0, 10);
		existsNameClient = client.getName();
		nonExistsNameClient = "Other Client";
		existsEmail = client.getEmail();
		nonExistsEmail = "bar@gmail.com";
		emptyEmail = "";
		existsCpf = client.getCpf();
		nonExistsCpf = "71301239070";
		emptyCpf ="";
		existsId = dto.getId();
		nonExistsId = 100L;
		dependentId = 3L;
		
		emptyNameClient = "";
		
		
		Mockito.doNothing().when(authentication).authUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
		
		Mockito.when(repository.getReferenceById(existsId)).thenReturn(client);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(client);	
		Mockito.when(repository.existsById(existsId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistsId)).thenReturn(false);
		Mockito.doNothing().when(repository).deleteById(existsId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(userService.getMe()).thenReturn(userMinDTO);
		Mockito.when(roleRepository.findByAuthority(ArgumentMatchers.anyString())).thenReturn(new Role(null, "ROLE_CLIENT"));
		Mockito.when(authRepository.findByAuth(ArgumentMatchers.anyString())).thenReturn(new Auth(null, "READER"));
		

		Mockito.when(userService.getMe()).thenReturn(userMinDTO);
	
		serviceSpy = Mockito.spy(service);
		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(ArgumentMatchers.any(), ArgumentMatchers.any());		
	}

	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenEmptyNameAndEmail() {
		Mockito.when(repository.searchAll(emptyNameClient, emptyEmail, emptyCpf, pageable))
				.thenReturn(new PageImpl<>(List.of(client, client, client)));


		Page<ClientMinDTO> result = service.findAll(emptyNameClient, emptyEmail, emptyCpf, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existsNameClient);
		Assertions.assertEquals(result.toList().get(1).getEmail(), existsEmail);
	}
	
	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenNotEmptyName() {
		Mockito.when(repository.searchAll(existsNameClient, emptyEmail, emptyCpf, pageable))
		.thenReturn(new PageImpl<>(List.of(client, client, client)));
		
		Page<ClientMinDTO> result = service.findAll(existsNameClient, emptyEmail, emptyCpf, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existsNameClient);
	}
	
	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenNotEmptyEmail() {
		Mockito.when(repository.searchAll(emptyNameClient, existsEmail, emptyCpf, pageable))
		.thenReturn(new PageImpl<>(List.of(client, client, client)));
		
		Page<ClientMinDTO> result = service.findAll(emptyNameClient, existsEmail, emptyCpf, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getEmail(), existsEmail);
	}
	
	@Test
	public void findAllShouldReturnPagedClientMinDTOWhenExistsNameAndEmailAndCPF() {
		Mockito.when(repository.searchAll(existsNameClient, existsEmail, existsCpf, pageable))
				.thenReturn(new PageImpl<>(List.of(client, client, client)));

		Page<ClientMinDTO> result = service.findAll(existsNameClient, existsEmail, existsCpf, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existsNameClient);
		Assertions.assertEquals(result.toList().get(1).getEmail(), existsEmail);
	}

	@Test
	public void findAllShouldfindAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsName() {
		Mockito.when(repository.searchAll(nonExistsNameClient,emptyEmail, emptyCpf, pageable)).thenReturn(new PageImpl<>(List.of()));

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistsNameClient,emptyEmail, emptyCpf,  pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsEmail() {
		Mockito.when(repository.searchAll(emptyNameClient,nonExistsEmail, emptyCpf, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(emptyNameClient,nonExistsEmail, emptyCpf, pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsNameAndEmailAndCpf() {
		Mockito.when(repository.searchAll(nonExistsNameClient,nonExistsEmail, nonExistsCpf, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistsNameClient,nonExistsEmail, nonExistsCpf, pageable);
		});
	}

	@Test
	public void findByIdShouldReturnClientMinDTOWhenExistsId() {
		Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(client));

		ClientMinDTO result = service.findById(existsId);

		Assertions.assertEquals(result.getId(), existsId);
		Assertions.assertEquals(result.getName(), client.getName());
		Assertions.assertEquals(result.getEmail(), client.getEmail());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistsId);
		});
	}

	@Test
	public void insertShouldReturnClientMinDTOWhenEmailIsUnique() {
		ClientMinDTO result = serviceSpy.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), client.getName());
		Assertions.assertEquals(result.getEmail(), client.getEmail());
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
		Assertions.assertEquals(result.getId(), existsId);
		Assertions.assertEquals(result.getName(), client.getName());
	}
	
	@Test
	public void updateShouldTrowEntityNotFoundExceptionWhenNonExistsClient() {
		Mockito.doThrow(EntityNotFoundException.class).when(repository).saveAndFlush(ArgumentMatchers.any());	
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.update(dto, existsId);
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
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());	
		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.update(dto, existsId);
		});
		
	}
	
	
	@Test 
	public void deleteShouldDoNothingWhenIdExistsAndIdDoesNotDependent() {
		Assertions.assertDoesNotThrow(() -> {
			serviceSpy.delete(existsId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.delete(nonExistsId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.delete(dependentId);
		});
	}
	
	@Test
	public void copyDtoToEntityShouldCopyAllDataDtoForClient(){
		service.copyDtoToEntity(dto, client);
	
		List<String> resultAuhts = client.getAuths().stream().map(x -> x.getAuth()).toList();
		List<String> resultRoles = client.getRoles().stream().map(x -> x.getAuthority()).toList();
		
		
		Assertions.assertEquals(dto.getName(), client.getName());
		Assertions.assertEquals(dto.getEmail(), client.getEmail());
		Assertions.assertEquals(dto.getCpf(), client.getCpf());
		Assertions.assertTrue(resultAuhts.contains("READER"));
		Assertions.assertTrue(resultRoles.contains("ROLE_CLIENT"));
	}
	
	@Test
	public void copyDtoToEntityShouldCopyAllDataDtoForClientWhenEmptyPassword(){
		dto.getPassword().isEmpty();
	
		service.copyDtoToEntity(dto, client);
		
		List<String> resultAuhts = client.getAuths().stream().map(x -> x.getAuth()).toList();
		List<String> resultRoles = client.getRoles().stream().map(x -> x.getAuthority()).toList();
		
		
		Assertions.assertEquals(dto.getName(), client.getName());
		Assertions.assertEquals(dto.getEmail(), client.getEmail());
		Assertions.assertEquals(dto.getCpf(), client.getCpf());
		Assertions.assertTrue(resultAuhts.contains("READER"));
		Assertions.assertTrue(resultRoles.contains("ROLE_CLIENT"));
	}
}
