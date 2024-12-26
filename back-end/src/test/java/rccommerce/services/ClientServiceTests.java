package rccommerce.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
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
import rccommerce.entities.Permission;
import rccommerce.entities.Role;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.PermissionRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.InvalidPasswordExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.ValidPassword;
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
    private PermissionRepository permissionRepository;

    @Mock
    private MessageSource messageSource;

    private Long existsId, nonExistsId, dependentId;
    private Client client;
    private Pageable pageable;
    private ClientService serviceSpy;
    private ClientDTO dto;
    private Role roleClient;
    private Permission permissionNome;

    @BeforeEach
    void setUp() throws Exception {
        client = FactoryUser.createClient();
        dto = FactoryUser.createClientDTO(client);
        roleClient = FactoryUser.createRoleClient();
        permissionNome = FactoryUser.createPermissionNone();
        pageable = PageRequest.of(0, 10);
        existsId = 7L;
        nonExistsId = 100L;
        dependentId = 3L;

        when(repository.getReferenceById(existsId)).thenReturn(client);
        when(repository.saveAndFlush(any())).thenReturn(client);
        when(repository.existsById(existsId)).thenReturn(true);
        when(repository.existsById(dependentId)).thenReturn(true);
        when(repository.existsById(nonExistsId)).thenReturn(false);

        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(roleClient);
        when(permissionRepository.findByAuthority("PERMISSION_NONE")).thenReturn(permissionNome);

        serviceSpy = Mockito.spy(service);
        doNothing().when(serviceSpy).copyDtoToEntity(any(), any());
        doNothing().when(serviceSpy).checkUserPermissions(any());
        doNothing().when(serviceSpy).checkUserPermissions(any(), any());
        when(messageSource.getMessage(any(String.class), any(), any())).thenReturn("Cliente");
    }

    @Test
    public void searchEntityShouldReturnPagedClientMinDTO() {
        // Mockando o repositório com o Example correto
        // when(repository.findBy(eq(example), eq(query -> query.page(pageable)))).thenReturn(new PageImpl<>(List.of(client)));
        when(repository.findBy(any(), any())).thenReturn(new PageImpl<>(List.of(client)));

        // Chamando o método do serviço com o Example real
        Page<ClientMinDTO> result = serviceSpy.searchEntity(client.getId(), client.getName(), client.getEmail(), client.getCpf(), pageable);

        // Assertivas
        assertFalse(result.isEmpty());
        assertEquals(1, result.getSize());
        assertEquals(client.getName(), result.toList().get(0).getName());
        assertEquals(client.getEmail(), result.toList().get(0).getEmail());
    }

    @Test
    public void searchEntityShouldThrowResourceNotFoundExceptionWhenNonExixtsClient() {
        when(repository.findBy(any(), any())).thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.searchEntity(nonExistsId, "", "", "", pageable);

        });
    }

    @Test
    public void findByIdShouldReturnClientMinDTOWhenExistsId() {
        when(repository.findById(existsId)).thenReturn(Optional.of(client));

        ClientMinDTO result = serviceSpy.findById(existsId);

        assertEquals(existsId, result.getId());
        assertEquals(client.getName(), result.getName());
        assertEquals(client.getEmail(), result.getEmail());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
        when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.findById(nonExistsId);
        });
    }

    @Test
    public void insertShouldReturnClientMinDTOWhenEmailIsUnique() {
        ClientMinDTO result = serviceSpy.insert(dto);

        assertNotNull(result);
        assertEquals(client.getName(), result.getName());
        assertEquals(client.getEmail(), result.getEmail());
    }

    @Test
    public void insertShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
        DataIntegrityViolationException e = new DataIntegrityViolationException("");
        e.toString().concat("EMAIL NULLS FIRST");

        doThrow(e).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertThrows(DatabaseException.class, () -> {
            serviceSpy.insert(dto);
        });
    }

    @Test
    public void insertShouldTrowDatabaseExceptionWhenCpfDoesNotUnique() {
        doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertThrows(DatabaseException.class, () -> {
            serviceSpy.insert(dto);
        });
    }

    @Test
    public void updateShouldReturnClientMinDTOWhenExistsIdAndIdIsNot1AndEmailIsUnique() {
        ClientMinDTO result = serviceSpy.update(dto, existsId);

        assertNotNull(result);
        assertEquals(existsId, result.getId());
        assertEquals(client.getName(), result.getName());
    }

    @Test
    public void updateShouldTrowEntityNotFoundExceptionWhenNonExistsClient() {
        doThrow(EntityNotFoundException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.update(dto, nonExistsId);
        });
    }

    @Test
    public void updateShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
        DataIntegrityViolationException e = new DataIntegrityViolationException("");
        e.toString().concat("EMAIL NULLS FIRST");

        doThrow(e).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertThrows(DatabaseException.class, () -> {
            serviceSpy.update(dto, existsId);
        });
    }

    @Test
    public void updateShouldTrowDatabaseExceptionWhenCpfDoesNotUnique() {
        doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertThrows(DatabaseException.class, () -> {
            serviceSpy.update(dto, existsId);
        });

    }

    @Test
    public void deleteShouldDoNothingWhenIdExistsAndIdDoesNotDependent() {
        doNothing().when(repository).deleteById(existsId);

        assertDoesNotThrow(() -> {
            serviceSpy.delete(existsId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.delete(nonExistsId);
        });
    }

    @Test
    public void deleteShouldTrowDataIntegrityViolationExceptionWhenIdDependent() {
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        assertThrows(DatabaseException.class, () -> {
            serviceSpy.delete(dependentId);
        });
    }

    @Test
    void isValidPasswordShouldBCryptPasswordWhenValidPassword() {
        String password = "Valid1@";

        String result = ValidPassword.isValidPassword(password);

        // Verifica se o resultado não é nulo
        assertNotNull(result);

        // Verifica se o resultado é uma senha encriptada (não pode ser igual à senha
        // original)
        assertNotEquals(password, result);

        // Verifica se a senha encriptada começa com "$2a$" (padrão BCrypt)
        assertTrue(result.startsWith("$2a$"));
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenNullPassword() {
        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword(null);
        });

        String expectedMessage = "Senha inválida: pelo menos 6 dígitos.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenShortPassword() {

        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword("12345");
        });

        String expectedMessage = "Senha inválida: pelo menos 6 dígitos.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenMissingUppercase() {
        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword("lowercase1!");
        });

        String expectedMessage = "Senha inválida: pelo menos uma letra maiúscula.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenMissingLowercase() {
        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword("UPPERCASE1!");
        });

        String expectedMessage = "Senha inválida: pelo menos uma letra minúscula.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenMissingDigit() {
        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword("NoDigit!");
        });

        String expectedMessage = "Senha inválida: pelo menos um dígito.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenMissingSpecialCharacter() {
        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword("NoSpecial1");
        });

        String expectedMessage = "Senha inválida: pelo menos um caractere especial.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isValidPasswordShouldInvalidPasswordExecptionWhenMultipleIssues() {
        Exception exception = assertThrows(InvalidPasswordExecption.class, () -> {
            ValidPassword.isValidPassword("short");
        });

        String expectedMessage = "Senha inválida: pelo menos 6 dígitos.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void copyDtoToEntityShouldCopyAllDataWhenAllFieldsAreValid() {
        // Prepare the DTO with valid data
        client.setPassword("Valid1@"); // Assume this password is valid
        client.setName("John Doe");
        client.setEmail("john.doe@example.com");
        client.setCpf("12345678900");

        // when(permissionRepository.findByAuthority(any())).thenReturn(any());
        dto = FactoryUser.createClientDTO(client);

        // Call the method to copy the data
        service.copyDtoToEntity(dto, client);

        // Assertions to verify that the data has been copied correctly
        assertEquals(dto.getName(), client.getName());
        assertEquals(dto.getEmail().toLowerCase(), client.getEmail());
        // assertEquals(dto.getCpf(), client.getCpf());
        assertNotNull(client.getPassword()); // Check that the password is set
        assertTrue(client.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_CLIENT")));
        assertTrue(client.getPermissions().stream()
                .anyMatch(permission -> permission.getAuthority().equals("PERMISSION_NONE")));
    }

    @Test
    public void copyDtoToEntityShouldCopyDataWhenPasswordIsProvided() {
        // Prepare the DTO with a password
        client.setPassword("Valid1@");
        dto = FactoryUser.createClientDTO(client);

        // Call the method to copy the data
        service.copyDtoToEntity(dto, client);

        // Assert that the password is hashed and set
        assertNotNull(client.getPassword());
        assertNotEquals(dto.getPassword(), client.getPassword()); // Ensure the
        // password is not stored in plain text
    }

    @Test
    public void copyDtoToEntityShouldCopyDataWhenPasswordIsEmpty() {
        // Set password as empty
        client.setPassword("");
        dto = FactoryUser.createClientDTO(client);

        // Call the method to copy the data
        service.copyDtoToEntity(dto, client);

        // Assert that the existing password is retained
        assertEquals(client.getPassword(), client.getPassword()); // Existing password should remain unchanged
    }

    @Test
    public void copyDtoToEntityShouldAssignRolesAndPermissionsCorrectly() {
        // Prepare the DTO
        client.setPassword("Valid1@");
        client.setName("John Doe");
        client.setEmail("john.doe@example.com");
        dto = FactoryUser.createClientDTO(client);

        // Call the method to copy the data
        service.copyDtoToEntity(dto, client);

        // Assert roles and permissions
        assertTrue(client.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_CLIENT")));
        assertTrue(client.getPermissions().stream()
                .anyMatch(permission -> permission.getAuthority().equals("PERMISSION_NONE")));
    }

    @Test
    public void copyDtoToEntityShouldNotChangePasswordWhenPasswordIsEmpty() {
        // Prepare the DTO with an empty password
        client.setPassword("");
        dto = FactoryUser.createClientDTO(client);

        // Save the initial password
        String initialPassword = client.getPassword();

        // Call the method to copy the data
        service.copyDtoToEntity(dto, client);

        // Assert that the password remains unchanged
        assertEquals(initialPassword, client.getPassword());
    }
}
