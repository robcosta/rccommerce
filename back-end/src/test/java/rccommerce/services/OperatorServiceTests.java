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

import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Verify;
import rccommerce.entities.Operator;
import rccommerce.entities.Role;
import rccommerce.repositories.VerifyRepository;
import rccommerce.repositories.OperatorRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.AuthService;
import rccommerce.tests.FactoryUser;

@ExtendWith(SpringExtension.class)
public class OperatorServiceTests {

	@InjectMocks
	private OperatorService service;

	@Mock
	private OperatorRepository repository;
	
	@Mock
	private VerifyRepository authRepository;
	
	@Mock
	private Verify authentication; 
	
	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserService userService;

	private String existsNameOperator, nonExistsNameOperator, emptyNameOperator;
	private String existsEmail, nonExistsEmail, emptyEmail;
	private long existsId, nonExistsId, dependentId;
	private Operator operator;
	Pageable pageable;	
	private OperatorService serviceSpy;
	private OperatorDTO dto;
	private UserMinDTO adminDTO, operatorDTO;
	

	@BeforeEach
	void setUp() throws Exception {
		operator = FactoryUser.createOperator();
		dto = FactoryUser.createOperatorDTO(operator);
		
		adminDTO = FactoryUser.createUserMinDTO();
		adminDTO.getRoles().clear();
		adminDTO.getRoles().add("ROLE_ADMIN");
		
		operatorDTO = FactoryUser.createUserMinDTO();
		operatorDTO.getRoles().clear();
		operatorDTO.getRoles().add("ROLE_OPERATOR");
		
		pageable = PageRequest.of(0, 10);
		existsNameOperator = operator.getName();
		nonExistsNameOperator = "Other Operator";
		existsEmail = operator.getEmail();
		nonExistsEmail = "bar@gmail.com";
		emptyEmail = "";
		existsId = dto.getId();
		nonExistsId = 100L;
		dependentId = 3L;
		
		emptyNameOperator = "";
		
		
		Mockito.doNothing().when(authentication).authUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
		
		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(operator);	
		Mockito.when(repository.existsById(existsId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistsId)).thenReturn(false);
		Mockito.doNothing().when(repository).deleteById(existsId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		Mockito.when(userService.getMe()).thenReturn(adminDTO);
	
		serviceSpy = Mockito.spy(service);
		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(ArgumentMatchers.any(), ArgumentMatchers.any());		
	}

	@Test
	public void findAllShouldReturnPagedOperatorMinDTOWhenEmptyNameAndEmail() {
		Mockito.when(repository.searchAll(emptyNameOperator, emptyEmail, pageable))
				.thenReturn(new PageImpl<>(List.of(operator, operator, operator)));


		Page<OperatorMinDTO> result = service.findAll(emptyNameOperator, emptyEmail, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existsNameOperator);
		Assertions.assertEquals(result.toList().get(1).getEmail(), existsEmail);
	}
	
	@Test
	public void findAllShouldReturnPagedOperatorMinDTOWhenNotEmptyNameAndEmptyEmail() {
		Mockito.when(repository.searchAll(existsNameOperator, emptyEmail, pageable))
		.thenReturn(new PageImpl<>(List.of(operator, operator, operator)));
		
		Page<OperatorMinDTO> result = service.findAll(existsNameOperator, emptyEmail, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existsNameOperator);
	}
	
	@Test
	public void findAllShouldReturnPagedOperatorMinDTOWhenEmptyNameAndNotEmptyEmail() {
		Mockito.when(repository.searchAll(emptyNameOperator, existsEmail, pageable))
		.thenReturn(new PageImpl<>(List.of(operator, operator, operator)));
		
		Page<OperatorMinDTO> result = service.findAll(emptyNameOperator, existsEmail, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getEmail(), existsEmail);
	}
	
	@Test
	public void findAllShouldReturnPagedOperatorMinDTOWhenExistsNameAndEmail() {
		Mockito.when(repository.searchAll(existsNameOperator, existsEmail, pageable))
				.thenReturn(new PageImpl<>(List.of(operator, operator, operator)));

		Page<OperatorMinDTO> result = service.findAll(existsNameOperator, existsEmail, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existsNameOperator);
		Assertions.assertEquals(result.toList().get(1).getEmail(), existsEmail);
	}

	@Test
	public void findAllShouldfindAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsName() {
		Mockito.when(repository.searchAll(nonExistsNameOperator,emptyEmail, pageable)).thenReturn(new PageImpl<>(List.of()));

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistsNameOperator,emptyEmail, pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsEmail() {
		Mockito.when(repository.searchAll(emptyNameOperator,nonExistsEmail, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(emptyNameOperator,nonExistsEmail, pageable);
		});
	}
	
	@Test
	public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsNameAndEmail() {
		Mockito.when(repository.searchAll(nonExistsNameOperator,nonExistsEmail, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistsNameOperator,nonExistsEmail, pageable);
		});
	}

	@Test
	public void findByIdShouldReturnOperatorMinDTOWhenExistsId() {
		Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(operator));

		OperatorMinDTO result = service.findById(existsId);

		Assertions.assertEquals(result.getId(), existsId);
		Assertions.assertEquals(result.getName(), operator.getName());
		Assertions.assertEquals(result.getEmail(), operator.getEmail());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistsId);
		});
	}

	@Test
	public void insertShouldReturnOperatorMinDTOWhenEmailIsUnique() {
		OperatorMinDTO result = serviceSpy.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), operator.getName());
		Assertions.assertEquals(result.getEmail(), operator.getEmail());
	}
	
	@Test
	public void insertShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());	
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.insert(dto);
		});
	}

	@Test
	public void updateShouldReturnOperatorMinDTOWhenExistsIdAndIdIsNot1AndEmailIsUnique() {
		OperatorMinDTO result = serviceSpy.update(dto, existsId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existsId);
		Assertions.assertEquals(result.getName(), operator.getName());
	}
	
	@Test
	public void updateShouldThrowForbiddenExceptionWhenIdIs1() {
		existsId = 1L;
		Assertions.assertThrows(ForbiddenException.class, () -> {
			serviceSpy.update(dto, existsId);
		});		
	}
	
	@Test
	public void updateShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
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
	public void deleteShouldThrowForbiddenExceptionWhenIdIs1() {
		existsId = 1L;
		Assertions.assertThrows(ForbiddenException.class, () -> {
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
	public void copyDtoToEntityShouldCopyAllDataDtoForOperator(){
		dto.getAuths().clear();
		dto.getAuths().add("CREATE");
		dto.getRoles().clear();
		dto.getRoles().add("ROLE_SELLER");
		
		Mockito.when(userService.getMe()).thenReturn(adminDTO);
		Mockito.when(roleRepository.findByAuthority(ArgumentMatchers.anyString())).thenReturn(new Role(null, "ROLE_SELLER"));
		Mockito.when(authRepository.findByAuth(ArgumentMatchers.anyString())).thenReturn(new Verify(null, "CREATE"));
		
		service.copyDtoToEntity(dto, operator);
	
		List<String> resultAuhts = operator.getAuths().stream().map(x -> x.getAuth()).toList();
		List<String> resultRoles = operator.getRoles().stream().map(x -> x.getAuthority()).toList();
		
		
		Assertions.assertEquals(dto.getName(), operator.getName());
		Assertions.assertEquals(dto.getEmail(), operator.getEmail());
		Assertions.assertEquals(dto.getCommission(), operator.getCommission());
		Assertions.assertTrue(resultAuhts.contains("CREATE"));
		Assertions.assertTrue(resultRoles.contains("ROLE_SELLER"));
	}
	
	@Test
	public void copyDtoToEntityShouldCopyAllDataDtoForOperatorWhenEmptyPassword(){
		dto.getAuths().clear();
		dto.getAuths().add("CREATE");
		dto.getRoles().clear();
		dto.getRoles().add("ROLE_SELLER");
		dto.getPassword().isEmpty();
			
		Mockito.when(userService.getMe()).thenReturn(adminDTO);
		Mockito.when(roleRepository.findByAuthority(ArgumentMatchers.anyString())).thenReturn(new Role(null, "ROLE_SELLER"));
		Mockito.when(authRepository.findByAuth(ArgumentMatchers.anyString())).thenReturn(new Verify(null, "CREATE"));
		
		service.copyDtoToEntity(dto, operator);
		
		List<String> resultAuhts = operator.getAuths().stream().map(x -> x.getAuth()).toList();
		List<String> resultRoles = operator.getRoles().stream().map(x -> x.getAuthority()).toList();
		
		
		Assertions.assertEquals(dto.getName(), operator.getName());
		Assertions.assertEquals(dto.getEmail(), operator.getEmail());
		Assertions.assertEquals(dto.getCommission(), operator.getCommission());
		Assertions.assertTrue(resultAuhts.contains("CREATE"));
		Assertions.assertTrue(resultRoles.contains("ROLE_SELLER"));
	}
	
	@Test
	public void copyDtoToEntityShouldCopyDataDtoForOperatorMinusRolesAndAuthWhenUserLoggerDoesNotAdmin(){
		dto.getAuths().clear();
		dto.getAuths().add("CREATE");
		dto.getRoles().clear();
		dto.getRoles().add("ROLE_SELLER");
				
		Mockito.when(userService.getMe()).thenReturn(operatorDTO);
		
		service.copyDtoToEntity(dto, operator);
		
		List<String> resultAuhts = operator.getAuths().stream().map(x -> x.getAuth()).toList();
		List<String> resultRoles = operator.getRoles().stream().map(x -> x.getAuthority()).toList();
		
		
		Assertions.assertEquals(dto.getName(), operator.getName());
		Assertions.assertEquals(dto.getEmail(), operator.getEmail());
		Assertions.assertEquals(dto.getCommission(), operator.getCommission());
		Assertions.assertFalse(resultAuhts.contains("CREATE"));
		Assertions.assertFalse(resultRoles.contains("ROLE_SELLER"));
	}
	
	@Test
	public void copyDtoToEntityShouldThowInvalidArgumentExecptionWhenAuthDoesNotExists(){
		dto.getAuths().clear();
		dto.getAuths().add("INEXISTENTE");
		dto.getRoles().clear();
		dto.getRoles().add("ROLE_SELLER");
		
		Mockito.when(userService.getMe()).thenReturn(adminDTO);		

		
		Assertions.assertThrows(InvalidArgumentExecption.class, () -> {
			service.copyDtoToEntity(dto, operator);
		});
	}
	
	@Test
	public void copyDtoToEntityShouldThowInvalidArgumentExecptionWhenRoleDoesNotExists(){
		dto.getAuths().clear();
		dto.getAuths().add("ALL");
		dto.getRoles().clear();
		dto.getRoles().add("INEXISTENTE");
		
		Mockito.when(userService.getMe()).thenReturn(adminDTO);
	
		Assertions.assertThrows(InvalidArgumentExecption.class, () -> {
			service.copyDtoToEntity(dto, operator);
		});
	}
	
	@Test
	public void copyDtoToEntityShouldThowInvalidArgumentExecptionWhenRoleIsEmpty(){
		dto.getAuths().clear();
		dto.getAuths().add("ALL");
		dto.getRoles().clear();	
		
		Mockito.when(userService.getMe()).thenReturn(adminDTO);
		
		Assertions.assertThrows(InvalidArgumentExecption.class, () -> {
			service.copyDtoToEntity(dto, operator);
		});
	}
}
