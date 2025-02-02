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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import rccommerce.dto.UserMinDTO;
import rccommerce.entities.User;
import rccommerce.repositories.UserRepository;
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
    private CustomUserUtil userUtil;

    private String existsUsername, nonExistsUsername;
    private String existsNameUser, nonExistsNameUser, emptyNameUser;
    private String existsEmail, nonExistsEmail, emptyEmail;
    private Long existsId, nonExistsId;
    private User user;
    private Pageable pageable;
    private UserService serviceSpy;

    @BeforeEach
    public void setUp() throws Exception {
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
        emptyNameUser = "";

        serviceSpy = Mockito.spy(service);
        // Mockito.doNothing().when(serviceSpy).copyDtoToEntity(dto, user);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExist() {
        Mockito.when(repository.searchUserRolesAndPermissionsByEmail(existsUsername)).thenReturn(Optional.of(user));

        User result = service.loadUserByUsername(existsUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldTrowUsernameNotFoundExceptionWhenDoesNotExistUser() {
        Mockito.when(repository.searchUserRolesAndPermissionsByEmail(nonExistsUsername)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistsUsername);
        });
        Assertions.assertEquals("Usuário não encontrado: " + nonExistsUsername, exception.getMessage());
    }

    @Test
    public void authenticatedShouldReturnUserWhenUserExixts() {
        when(userUtil.getLoggerUsername()).thenReturn(existsUsername);
        when(repository.searchUserRolesAndPermissionsByEmail(existsUsername)).thenReturn(Optional.of(user));

        User result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existsUsername);
    }

    @Test
    public void authenticatedShouldThrowResourceNotFoundExceptionWhenDoesNotExixtUser() {
        when(repository.searchUserRolesAndPermissionsByEmail(nonExistsUsername)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.authenticated();
        });
        Assertions.assertEquals("Usuário não encontrado: null", exception.getMessage());
    }

    @Test
    public void getMeShouldReturnUserMinDTOWhenUserAuthenticated() {
        // Mockito.when(repository.searchUserRolesAndPermissionsByEmail(existsUsername)).thenReturn(Optional.of(user));
        Mockito.doReturn(user).when(serviceSpy).authenticated();

        UserMinDTO result = serviceSpy.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getEmail(), existsUsername);
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotAuthenticated() {
        Mockito.doThrow(ResourceNotFoundException.class).when(serviceSpy).authenticated();

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.getMe();
        });
        Assertions.assertNotNull(exception.getMessage());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenEmptyNameAndEmail() {

        Mockito.when(repository.searchAll(emptyNameUser, emptyEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = serviceSpy.searchEntity(emptyNameUser, emptyEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getSize());
        Assertions.assertEquals(existsNameUser, result.toList().get(0).getName());
        Assertions.assertEquals(existsEmail, result.toList().get(1).getEmail());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenNotEmptyNameAndEmptyEmail() {
        Mockito.when(repository.searchAll(existsNameUser, emptyEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = service.searchEntity(existsNameUser, emptyEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getSize());
        Assertions.assertEquals(existsNameUser, result.toList().get(0).getName());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenEmptyNameAndNotEmptyEmail() {
        Mockito.when(repository.searchAll(emptyNameUser, existsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user)));

        Page<UserMinDTO> result = service.searchEntity(emptyNameUser, existsEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getSize());
        Assertions.assertEquals(existsEmail, result.toList().get(0).getEmail());
    }

    @Test
    public void findAllShouldReturnPagedUserMinDTOWhenExistsNameAndEmail() {
        Mockito.when(repository.searchAll(existsNameUser, existsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(user, user, user)));

        Page<UserMinDTO> result = service.searchEntity(existsNameUser, existsEmail, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3L, result.getSize());
        Assertions.assertEquals(existsNameUser, result.toList().get(0).getName());
        Assertions.assertEquals(existsEmail, result.toList().get(1).getEmail());
    }

    @Test
    public void findAllShouldfindAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsName() {
        Mockito.when(repository.searchAll(nonExistsNameUser, emptyEmail, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.searchEntity(nonExistsNameUser, emptyEmail, pageable);
        });
        Assertions.assertEquals("Recurso não encontrado", exception.getMessage());
    }

    @Test
    public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsEmail() {
        when(userUtil.getLoggerUsername()).thenReturn(existsUsername);
        when(repository.searchUserRolesAndPermissionsByEmail(existsUsername)).thenReturn(Optional.of(user));
        when(repository.searchAll(emptyNameUser, nonExistsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.searchEntity(emptyNameUser, nonExistsEmail, pageable);
        });
        Assertions.assertEquals("Recurso não encontrado", exception.getMessage());
    }

    @Test
    public void findAllShouldTrhowResouceNotFoundExceptionWhenDoesNotExistsNameAndEmail() {
        Mockito.when(repository.searchAll(nonExistsNameUser, nonExistsEmail, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.searchEntity(nonExistsNameUser, nonExistsEmail, pageable);
        });
        Assertions.assertEquals("Recurso não encontrado", exception.getMessage());
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
        Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistsId);
        });
        Assertions.assertEquals("Usuário não encontrado: " + nonExistsId, exception.getMessage());
    }

    @Test
    public void findByEmailShouldReturnUserMinDTOWhenExistsEmail() {
        Mockito.when(repository.searchUserRolesAndPermissionsByEmail(existsEmail)).thenReturn(Optional.of(user));

        UserMinDTO result = service.findByEmail(existsEmail);

        Assertions.assertEquals(existsId, result.getId());
        Assertions.assertEquals(user.getName(), result.getName());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void findByEmailShouldThrowResourceNotFoundExceptionWhenDoesNotExistsEmail() {
        Mockito.when(repository.searchUserRolesAndPermissionsByEmail(nonExistsEmail))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findByEmail(nonExistsEmail);
        });
        Assertions.assertEquals("Usuário não encontrado: " + nonExistsEmail, exception.getMessage());
    }
}
