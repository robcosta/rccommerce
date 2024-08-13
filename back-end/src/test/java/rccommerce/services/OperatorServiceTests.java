package rccommerce.services;

import static org.hamcrest.CoreMatchers.any;

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
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Operator;
import rccommerce.entities.User;
import rccommerce.repositories.OperatorRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.tests.FactoryUser;

@ExtendWith(SpringExtension.class)
public class OperatorServiceTests {

	@InjectMocks
	private OperatorService service;

	@Mock
	private OperatorRepository repository;

	@Mock
	private UserService userService;

	private String existsNameOperator, nonExistsNameOperator, emptyNameOperator;
	private String existsEmail, nonExistsEmail, emptyEmail;
	private long idMaster, existsId, nonExistingId, integrityViolationId;
	private Operator operator;
	Pageable pageable;	
	private OperatorService serviceSpy;
	private OperatorDTO dto;
	private User user;
	private UserMinDTO userMinDTO;
	

	@BeforeEach
	void setUp() throws Exception {
		idMaster = 1L;
		operator = FactoryUser.createOperator();
		dto = FactoryUser.createOperatorDTO(operator);
		user = FactoryUser.createUser();
		userMinDTO = FactoryUser.createUserMinDTO();
		pageable = PageRequest.of(0, 10);
		existsNameOperator = operator.getName();
		nonExistsNameOperator = "Other Operator";
		existsEmail = operator.getEmail();
		nonExistsEmail = "bar@gmail.com";
		emptyEmail = "";
		existsId = operator.getId();
		nonExistingId = 100L;
		integrityViolationId = 2L;
		emptyNameOperator = "";

		serviceSpy = Mockito.spy(service);
		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, operator);
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
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void insertShouldReturnOperatorMinDTOWhenEmailIsUnique() {
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(operator);

		OperatorMinDTO result = service.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), operator.getName());
		Assertions.assertEquals(result.getEmail(), operator.getEmail());
	}

	@Test
	public void insertShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).save(ArgumentMatchers.any());

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.insert(dto);
		});
	}

	@Test
	public void updateShouldReturnOperatorMinDTOWhenExistsIdAndUserLoggedIsAdmin() {
		user.addRole(FactoryUser.createRoleAdmin());
		userMinDTO = FactoryUser.createUserMinDTO(user);
		
		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(operator);
		Mockito.when(userService.getMe()).thenReturn(userMinDTO);

		OperatorMinDTO result = serviceSpy.update(dto, existsId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existsId);
		Assertions.assertEquals(result.getName(), operator.getName());
	}
	
	@Test
	public void updateShouldReturnOperatorMinDTOWhenUserLoggedNotAdminAndSelfeId() {
		user.addRole(FactoryUser.createRoleOperator());
		userMinDTO = FactoryUser.createUserMinDTO(user);
		operator = FactoryUser.createOperator(user);
		dto = FactoryUser.createOperatorDTO(operator);
		existsId = dto.getId();
		
		Mockito.when(userService.getMe()).thenReturn(userMinDTO);
		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
		Mockito.when(repository.saveAndFlush(operator)).thenReturn(operator);
		
		OperatorMinDTO result = service.update(dto, existsId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existsId);
		Assertions.assertEquals(result.getName(), operator.getName());
	}
	
	@Test
	public void updateShouldReturnOperatorMinDTOWhenUserLoggedNotAdminAndNonSelfeId() {
		user.addRole(FactoryUser.createRoleOperator());
		userMinDTO = FactoryUser.createUserMinDTO(user);
		operator = FactoryUser.createOperator(user);
		existsId = dto.getId();
		
		Mockito.when(userService.getMe()).thenReturn(userMinDTO);
		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			serviceSpy.update(dto, existsId);
		});

	}
	
//	@Test
//	public void updateShouldReturnOperatorMinDTOWhenExistsIdAndEmptyPassword() {
//		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
//		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(operator);
//		operator.setPassword("");
//		dto = Factory.createOperatorDTO(operator);
//		
//		OperatorMinDTO result = serviceSpy.update(dto, existsId);
//		
//		Assertions.assertNotNull(result);
//		Assertions.assertEquals(result.getId(), existsId);
//		Assertions.assertEquals(result.getName(), operator.getName());
//	}
//
//	@Test
//	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
//		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
//		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//			serviceSpy.update(dto, nonExistingId);
//		});
//
//	}
//
//	@Test
//	public void updateShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
//		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
//		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(operator);
//
//		Assertions.assertThrows(DatabaseException.class, () -> {
//			serviceSpy.update(dto, existsId);
//		});
//	}
//
//	@Test
//	public void updateShouldVerifiPaswordWhenPassswordNotEmpty() {
//		Mockito.when(repository.getReferenceById(existsId)).thenReturn(operator);
//		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(operator);
//
//		OperatorMinDTO result = serviceSpy.update(dto, existsId);
//
//		Assertions.assertNotNull(result);
//		Assertions.assertEquals(result.getId(), existsId);
//		Assertions.assertEquals(result.getName(), operator.getName());
//	}
//
//	@Test
//	public void deleteShouldDoNothingWhenExixstsId() {
//		Operator operatorLogged = operator;
//		operatorLogged.setId(20L);
//		Mockito.doReturn(operatorLogged).when(userService).authenticated();
//		Mockito.when(repository.existsById(existsId)).thenReturn(true);
//		Mockito.doNothing().when(repository).deleteById(existsId);
//
//		Assertions.assertDoesNotThrow(() -> {
//			serviceSpy.delete(existsId);
//		});
//	}
//
//	@Test
//	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
//		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
//
//		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//			service.delete(nonExistingId);
//		});
//
//	}
//
//	@Test
//	public void deleteShoulThronForbiddenExceptionWhenTryToDeleteLoggedInOperator() {
//		Mockito.when(repository.existsById(existsId)).thenReturn(true);
//		Mockito.doReturn(operator).when(userService).authenticated();
//
//		Assertions.assertThrows(ForbiddenException.class, () -> {
//			serviceSpy.delete(existsId);
//		});
//	}
//
//	@Test
//	public void deleteShouldThrowDatabaseExceptionWhenBreachOfIntegrity() {
//		Mockito.when(repository.existsById(integrityViolationId)).thenReturn(true);
//		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(integrityViolationId);
//		Mockito.doReturn(operator).when(userService).authenticated();
//
//		Assertions.assertThrows(DatabaseException.class, () -> {
//			serviceSpy.delete(integrityViolationId);
//		});
//	}
//
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
