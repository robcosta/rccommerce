package rccommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import rccommerce.dto.UserMinDTO;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.UserRepository;
import rccommerce.tests.Factory;
import rccommerce.util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	@Mock CustomUserUtil userUtil;
	
	private  String existingUsername, nonExistingUsername;
	private User user;
	Pageable pageable;
	private List<UserDetailsProjection> userDetails;
	private UserService serviceSpy;
	
	@BeforeEach
	void setUp() throws Exception {
		user = Factory.createUser();
		pageable = PageRequest.of(0, 10);
		existingUsername = user.getEmail();
		nonExistingUsername = "user@gmail.com";
		userDetails = Factory.createUserDetails();
		serviceSpy = Mockito.spy(service);
		
		Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
		Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(List.of());
		
		Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
		Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());
			
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

}
