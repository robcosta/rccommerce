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

import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.RoleRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.DatabaseException;
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
	CustomUserUtil userUtil;
	
	
	
	private  String existingUsername, nonExistingUsername, existingNameUser, nonExistingNameUser, emptyNameUser;
	private long existingId, nonExistingId;
	private User user;
	private User user2;
	Pageable pageable;
	private List<UserDetailsProjection> userDetails;
	private UserService serviceSpy;
	private UserDTO dto;
	
	@BeforeEach
	void setUp() throws Exception {
		user = Factory.createUser();
		user2 = Factory.createUser();
		pageable = PageRequest.of(0, 10);
		existingUsername = user.getEmail();
		nonExistingUsername = "user@gmail.com";
		existingNameUser = user.getName();
		nonExistingNameUser = "Other User";
		emptyNameUser = "";
		existingId = user.getId();
		nonExistingId = 100L;
		userDetails = Factory.createUserDetails();
		serviceSpy = Mockito.spy(service);
		
		Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
		Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(List.of());
		
		Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
		Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());
		
		Mockito.when(repository.searchByName(existingNameUser, pageable)).thenReturn(new PageImpl<>(List.of(user)));
		Mockito.when(repository.searchByName(emptyNameUser, pageable)).thenReturn(new PageImpl<>(List.of(user, user, user)));
		Mockito.when(repository.searchByName(nonExistingNameUser, pageable)).thenReturn(new PageImpl<>(List.of()));
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(user);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).save(user2);
	}
	
	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExist() {
		UserDetails result = service.loadUserByUsername(existingUsername);
		
		Assertions.assertNotNull(result);	
		Assertions.assertEquals(result.getUsername(), user.getUsername());
		
	}
	
	@Test
	public void loadUserByUsernameShouldTrowUsernameNotFoundExceptionWhenDoesNotExistUser() {
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUsername);
		});		
	}
	
	@Test
	public void authenticatedShouldReturnUserWhenUserExixts() {
		Mockito.when(userUtil.getLoggerUsername()).thenReturn(existingUsername);
		User result = service.authenticated();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}
	
	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenDoesNotExixtUser() {
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggerUsername();
		
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
	public void findAllShouldPagedUserMinDTOWhenExistsUserName() {

		Page<UserMinDTO> result = service.findAll(existingNameUser, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getName(),existingNameUser);
	}
	
	@Test
	public void findAllShouldPagedUserMinDTOWhenUserNameIsEmpty() {

		Page<UserMinDTO> result = service.findAll(emptyNameUser, pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(result.getSize(), 3);
		Assertions.assertEquals(result.toList().get(0).getName(),existingNameUser);
		Assertions.assertEquals(result.toList().get(1).getName(),existingNameUser);
	}
	
	@Test
	public void findAllShouldPagedUserMinDTOWhenDoesNotExistsUserName() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			 service.findAll(nonExistingNameUser, pageable);
		});
	}
	
	@Test
	public void findByIdShouldUserMinDTOWhenExistsId() {
		UserMinDTO result = service.findById(existingId);
		
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), user.getName());
		Assertions.assertEquals(result.getEmail(), user.getEmail());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		} );
	}
	
	@Test 
	void insertShouldReturnUserMinDTOWhenEmailIsUnique() {
		user.setId(null);
		dto = Factory.createUserDTO(user);
		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, user);		
		
		UserMinDTO result = service.insert(dto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), existingNameUser);
		Assertions.assertEquals(result.getEmail(), user.getEmail());
	}
	
	@Test 
	void insertShouldDatabaseExceptionWhenEmailAlreadyRegistered() {
		user2.setId(null);
		dto = Factory.createUserDTO(user2);
		Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, user2);		
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.insert(dto);
		});
	}
}
