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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.RoleRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.InvalidPasswordExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.tests.Factory;
import rccommerce.util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private CustomUserUtil userUtil;

	private String existingUsername, nonExistingUsername;
	private String existingNameUser, emptyNameUser,nonExistingNameUser;
	private String passwordEmpty, passwordNotEmpty;
	private long existingId, nonExistingId, integrityViolationId;
	private User user;
	Pageable pageable;
	private List<UserDetailsProjection> userDetails;
	private UserService serviceSpy;
	private UserDTO dto;

	@BeforeEach
	void setUp() throws Exception {
		user = Factory.createUser();
		dto = Factory.createUserDTO(user);
		pageable = PageRequest.of(0, 10);
		existingUsername = user.getEmail();
		nonExistingUsername = "user@gmail.com";
		existingNameUser = user.getName();
		nonExistingNameUser = "Other User";
		existingId = user.getId();
		nonExistingId = 100L;
		integrityViolationId = 2L;
		userDetails = Factory.createUserDetails();
		emptyNameUser = "";
		passwordEmpty = "";
		passwordNotEmpty = "123456";

		serviceSpy = Mockito.spy(service);
		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, user);
		Mockito.when(serviceSpy.checkPassword(passwordNotEmpty)).thenReturn(true);
		Mockito.doThrow(InvalidPasswordExecption.class).when(serviceSpy).checkPassword(passwordEmpty);
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExist() {
		Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);

		UserDetails result = service.loadUserByUsername(existingUsername);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), user.getUsername());

	}

	@Test
	public void loadUserByUsernameShouldTrowUsernameNotFoundExceptionWhenDoesNotExistUser() {
		Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(List.of());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUsername);
		});
	}

	@Test
	public void authenticatedShouldReturnUserWhenUserExixts() {
		Mockito.when(userUtil.getLoggerUsername()).thenReturn(existingUsername);
		Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));

		User result = service.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenDoesNotExixtUser() {
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggerUsername();
		Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void getMeShouldReturnUserDTOWhenUserAuthenticated() {
		Mockito.doReturn(user).when(serviceSpy).authenticated();
		UserMinDTO result = serviceSpy.getMe();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getEmail(), existingUsername);
	}

	@Test
	public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotAuthenticated() {
		Mockito.doThrow(UsernameNotFoundException.class).when(serviceSpy).authenticated();

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.getMe();
		});
	}

	@Test
	public void findAllShouldReturnPagedUserMinDTOWhenExistsUserName() {
		Mockito.when(repository.searchByName(existingNameUser, pageable)).thenReturn(new PageImpl<>(List.of(user)));

		Page<UserMinDTO> result = service.findAll(existingNameUser, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getName(), existingNameUser);
	}

	@Test
	public void findAllShouldReturnPagedUserMinDTOWhenUserNameIsEmpty() {
		Mockito.when(repository.searchByName(emptyNameUser, pageable))
				.thenReturn(new PageImpl<>(List.of(user, user, user)));

		Page<UserMinDTO> result = service.findAll(emptyNameUser, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(), existingNameUser);
		Assertions.assertEquals(result.toList().get(1).getName(), existingNameUser);
	}

	@Test
	public void findAllShouldReturnPagedUserMinDTOWhenDoesNotExistsUserName() {
		Mockito.when(repository.searchByName(nonExistingNameUser, pageable)).thenReturn(new PageImpl<>(List.of()));

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findAll(nonExistingNameUser, pageable);
		});
	}

	@Test
	public void findByIdShouldReturnUserMinDTOWhenExistsId() {
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));

		UserMinDTO result = service.findById(existingId);

		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), user.getName());
		Assertions.assertEquals(result.getEmail(), user.getEmail());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void insertShouldReturnUserMinDTOWhenEmailIsUnique() {
		user.setId(null);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(user);

		UserMinDTO result = service.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), existingNameUser);
		Assertions.assertEquals(result.getEmail(), user.getEmail());
	}

	@Test
	public void insertShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
		user.setId(null);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).save(user);

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.insert(dto);
		});
	}

	@Test
	public void updateShouldReturnUserMinDTOWhenExistsId() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(user);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(user);

		UserMinDTO result = serviceSpy.update(dto, existingId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), user.getName());
	}
	
	@Test
	public void updateShouldReturnUserMinDTOWhenExistsIdAndEmptyPassword() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(user);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(user);
		user.setPassword("");
		dto = Factory.createUserDTO(user);
		
		UserMinDTO result = serviceSpy.update(dto, existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), user.getName());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			serviceSpy.update(dto, nonExistingId);
		});

	}

	@Test
	public void updateShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(user);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(user);

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.update(dto, existingId);
		});
	}

	@Test
	public void updateShouldVerifiPaswordWhenPassswordNotEmpty() {
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(user);
		Mockito.when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(user);

		UserMinDTO result = serviceSpy.update(dto, existingId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), user.getName());
	}

	@Test
	public void deleteShouldDoNothingWhenExixstsId() {
		User userLogged = user;
		userLogged.setId(20L);
		Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
		Mockito.doReturn(userLogged).when(serviceSpy).authenticated();
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.doNothing().when(repository).deleteById(existingId);

		Assertions.assertDoesNotThrow(() -> {
			serviceSpy.delete(existingId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

	}

	@Test
	public void deleteShoulThronForbiddenExceptionWhenTryToDeleteLoggedInUser() {
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.doReturn(user).when(serviceSpy).authenticated();

		Assertions.assertThrows(ForbiddenException.class, () -> {
			serviceSpy.delete(existingId);
		});
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenBreachOfIntegrity() {
		Mockito.when(repository.existsById(integrityViolationId)).thenReturn(true);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(integrityViolationId);
		Mockito.doReturn(user).when(serviceSpy).authenticated();

		Assertions.assertThrows(DatabaseException.class, () -> {
			serviceSpy.delete(integrityViolationId);
		});
	}

	@Test
	public void checkPassordShouldThrowInvalidPasswordExecptionWhenPasswordLessThanFuorCaracters() {
		String password = "123";

		Assertions.assertThrows(InvalidPasswordExecption.class, () -> {
			service.checkPassword(password);
		});
	}
	
	public void checkPassordShouldThrowInvalidPasswordExecptionWhenPasswordGreaterThanEighCaracters() {
		String password = "123456789";

		Assertions.assertThrows(InvalidPasswordExecption.class, () -> {
			service.checkPassword(password);
		});
	}
	
	@Test
	public void checkPassordShouldThrowInvalidPasswordExecptionWhenPasswordPasswordIsNotPositiveInteger() {
		String password = "A345-4";
		
		Assertions.assertThrows(InvalidPasswordExecption.class, () -> {
			service.checkPassword(password);
		});
	}

	@Test
	public void checkPassordShouldTrueWithPasswordIsOk() {
		String password = "123456";

		boolean result = serviceSpy.checkPassword(password);

		Assertions.assertTrue(result);
	}
}
