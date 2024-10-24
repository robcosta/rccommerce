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
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import rccommerce.dto.UserMinDTO;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.RoleRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.tests.FactoryUser;
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

    private String existsUsername, nonExistsUsername;
    private String existsNameUser, nonExistsNameUser, emptyNameUser;
    private String existsEmail, nonExistsEmail, emptyEmail;
    private Long existsId, nonExistsId;
    private User user;
    private Pageable pageable;
    private List<UserDetailsProjection> userDetails;
    private UserService serviceSpy;

    private ResourceNotFoundException assertNotFound;
    private DatabaseException assertDatabase;
    private UsernameNotFoundException assertUserNameNotFound;

    @BeforeEach
    void setUp() throws Exception {
        user = FactoryUser.createUser();
        pageable = PageRequest.of(0, 10);
        existsUsername = user.getEmail();
        nonExistsUsername = "bar@gmail.com";
        existsNameUser = user.getName();
        nonExistsNameUser = "Other User";
        existsEmail = user.getEmail();
        nonExistsEmail = "bar@gmail.com";
        emptyEmail = "";
        existsId = user.getId();
        nonExistsId = 100L;
        userDetails = FactoryUser.createUserDetails();
        emptyNameUser = "";

        serviceSpy = Mockito.spy(service);
        //Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, user);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExist() {
        Mockito.when(repository.searchUserRolesAndPermissionsByEmail(existsUsername)).thenReturn(userDetails);

        UserDetails result = service.loadUserByUsername(existsUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldTrowUsernameNotFoundExceptionWhenDoesNotExistUser() {
        Mockito.when(repository.searchUserRolesAndPermissionsByEmail(nonExistsUsername)).thenReturn(List.of());

        assertUserNameNotFound = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistsUsername);
        });
    }

    @Test
    public void authenticatedShouldReturnUserWhenUserExixts() {
        when(userUtil.getLoggerUsername()).thenReturn(existsUsername);
        when(repository.searchEmail(existsUsername)).thenReturn(Optional.of(user));

        User result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existsUsername);
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenDoesNotExixtUser() {
        Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggerUsername();
        Mockito.when(repository.findByEmail(nonExistsUsername)).thenReturn(Optional.empty());

        assertUserNameNotFound = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserAuthenticated() {
        Mockito.doReturn(user).when(serviceSpy).authenticated();

        UserMinDTO result = serviceSpy.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getEmail(), existsUsername);
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotAuthenticated() {
        Mockito.doThrow(UsernameNotFoundException.class).when(serviceSpy).authenticated();
        assertUserNameNotFound = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.getMe();
        });
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenEmptyNameAndEmail() {
        Mockito.when(repository.searchAll(emptyNameUser, emptyEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = service.findAll(emptyNameUser, emptyEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3L, result.getSize());
        Assertions.assertEquals(existsNameUser, result.toList().get(0).getName());
        Assertions.assertEquals(existsEmail, result.toList().get(1).getEmail());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenNotEmptyNameAndEmptyEmail() {
        Mockito.when(repository.searchAll(existsNameUser, emptyEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = service.findAll(existsNameUser, emptyEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getSize());
        Assertions.assertEquals(existsNameUser, result.toList().get(0).getName());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenEmptyNameAndNotEmptyEmail() {
        Mockito.when(repository.searchAll(emptyNameUser, existsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = service.findAll(emptyNameUser, existsEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getSize());
        Assertions.assertEquals(existsEmail, result.toList().get(0).getEmail());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenExistsNameAndEmail() {
        Mockito.when(repository.searchAll(existsNameUser, existsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = service.findAll(existsNameUser, existsEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3L, result.getSize());
        Assertions.assertEquals(existsNameUser, result.toList().get(0).getName());
        Assertions.assertEquals(existsEmail, result.toList().get(1).getEmail());
    }

    @Test
    public void findAllShouldfindAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsName() {
        Mockito.when(repository.searchAll(nonExistsNameUser, emptyEmail, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findAll(nonExistsNameUser, emptyEmail, pageable);
        });
    }

    @Test
    public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsEmail() {
        Mockito.when(repository.searchAll(emptyNameUser, nonExistsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findAll(emptyNameUser, nonExistsEmail, pageable);
        });
    }

    @Test
    public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsNameAndEmail() {
        Mockito.when(repository.searchAll(nonExistsNameUser, nonExistsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of()));
        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findAll(nonExistsNameUser, nonExistsEmail, pageable);
        });
    }

    @Test
    public void findByIdShouldReturnUserMinDTOWhenExistsId() {
        Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(user));

        UserMinDTO result = service.findById(existsId);

        Assertions.assertEquals(existsId, result.getId());
        Assertions.assertEquals(user.getName(), result.getName());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
        Mockito.doThrow(ResourceNotFoundException.class)
                .when(repository)
                .findById(nonExistsId);

        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistsId);
        });
    }

    @Test
    public void findByEmailShouldReturnUserMinDTOWhenExistsEmail() {
        Mockito.when(repository.findByEmail(existsEmail)).thenReturn(Optional.of(user));

        UserMinDTO result = service.findByEmail(existsEmail);

        Assertions.assertEquals(existsId, result.getId());
        Assertions.assertEquals(user.getName(), result.getName());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void findByEmailShouldThrowResourceNotFoundExceptionWhenDoesNotExistsEmail() {
        Mockito.doThrow(ResourceNotFoundException.class)
                .when(repository)
                .findByEmail(nonExistsEmail);
       assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findByEmail(nonExistsEmail);
        });
    }
}
