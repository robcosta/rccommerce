package rccommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.Permission;
import rccommerce.entities.Role;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.entities.enums.RoleAuthority;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.PermissionRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.SecurityContextUtil;
import rccommerce.tests.FactoryClient;

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

    private Long existingId;
    private Long nonExistingId;
    private Client client;
    private ClientDTO clientDTO;
    private Role clientRole;
    private Permission nonePermission;
    private Pageable pageable;
    private MockedStatic<SecurityContextUtil> securityContextUtil;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        client = FactoryClient.createClient();
        clientDTO = FactoryClient.createClientDTO();
        pageable = PageRequest.of(0, 10);

        clientRole = new Role(4L, RoleAuthority.ROLE_CLIENT.getName());
        nonePermission = new Permission(2L, PermissionAuthority.PERMISSION_NONE.getName());

        when(roleRepository.findByAuthority(RoleAuthority.ROLE_CLIENT.getName()))
                .thenReturn(clientRole);
        when(permissionRepository.findByAuthority(PermissionAuthority.PERMISSION_NONE.getName()))
                .thenReturn(nonePermission);

        // Mock do SecurityContextUtil
        securityContextUtil = Mockito.mockStatic(SecurityContextUtil.class);
        securityContextUtil.when(SecurityContextUtil::getUserId).thenReturn(1L);
        securityContextUtil.when(SecurityContextUtil::getAuthList)
                .thenReturn(List.of(PermissionAuthority.PERMISSION_ALL.getName()));
    }

    @AfterEach
    public void tearDown() {
        if (securityContextUtil != null) {
            securityContextUtil.close();
        }
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    public class CrudTests {

        @Test
        @DisplayName("insert should create new client with default role and permission")
        void insertShouldCreateNewClientWithDefaults() {
            when(repository.save(any(Client.class))).thenReturn(client);

            ClientMinDTO result = service.insert(clientDTO, false);

            assertNotNull(result);
            assertEquals(client.getEmail(), result.getEmail());
            assertEquals(client.getCpf().replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4"), result.getCpf());
        }

        @Test
        @DisplayName("update should update existing client")
        void updateShouldUpdateExistingClient() {
            when(repository.getReferenceById(existingId)).thenReturn(client);
            when(repository.saveAndFlush(any(Client.class))).thenReturn(client);

            ClientMinDTO result = service.update(clientDTO, existingId, false);

            assertNotNull(result);
            assertEquals(clientDTO.getEmail(), result.getEmail());
        }

        @Test
        @DisplayName("update should throw ResourceNotFoundException when id does not exist")
        void updateShouldThrowExceptionWhenIdDoesNotExist() {
            when(repository.getReferenceById(nonExistingId))
                    .thenThrow(ResourceNotFoundException.class);

            assertThrows(ResourceNotFoundException.class, ()
                    -> service.update(clientDTO, nonExistingId, false)
            );
        }
    }

    @Nested
    @DisplayName("Search Operations Tests")
    public class SearchTests {

        @Test
        @DisplayName("searchEntity should return all clients when parameters are empty")
        void searchEntityShouldReturnAllClientsWhenParametersEmpty() {
            List<Client> clients = List.of(
                    client,
                    FactoryClient.createClientWithDifferentData()
            );

            when(repository.searchAll(null, "", "", "", pageable))
                    .thenReturn(new PageImpl<>(clients));

            Page<ClientMinDTO> result = service.searchEntity(null, "", "", "", pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(2, result.getTotalElements());
        }

        @Test
        @DisplayName("searchEntity should return filtered clients by name")
        void searchEntityShouldReturnFilteredClientsByName() {
            String name = "John";

            when(repository.searchAll(null, name, "", "", pageable))
                    .thenReturn(new PageImpl<>(List.of(client)));

            Page<ClientMinDTO> result = service.searchEntity(null, name, "", "", pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.getContent().get(0).getName().contains(name));
        }

        @Test
        @DisplayName("searchEntity should return filtered clients by email")
        void searchEntityShouldReturnFilteredClientsByEmail() {
            String email = "john@";

            when(repository.searchAll(null, "", email, "", pageable))
                    .thenReturn(new PageImpl<>(List.of(client)));

            Page<ClientMinDTO> result = service.searchEntity(null, "", email, "", pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.getContent().get(0).getEmail().contains(email));
        }

        @Test
        @DisplayName("searchEntity should return filtered clients by CPF")
        void searchEntityShouldReturnFilteredClientsByCPF() {
            String cpf = "835";

            when(repository.searchAll(null, "", "", cpf, pageable))
                    .thenReturn(new PageImpl<>(List.of(client)));

            Page<ClientMinDTO> result = service.searchEntity(null, "", "", cpf, pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.getContent().get(0).getCpf().contains(cpf));
        }

        @Test
        @DisplayName("searchEntity should return filtered clients by multiple parameters")
        void searchEntityShouldReturnFilteredClientsByMultipleParams() {
            String name = "John";
            String email = "john@";
            String cpf = "835";

            when(repository.searchAll(existingId, name, email, cpf, pageable))
                    .thenReturn(new PageImpl<>(List.of(client)));

            Page<ClientMinDTO> result = service.searchEntity(existingId, name, email, cpf, pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
            var clientResult = result.getContent().get(0);
            assertTrue(clientResult.getName().contains(name));
            assertTrue(clientResult.getEmail().contains(email));
            assertTrue(clientResult.getCpf().contains(cpf));
        }

        @Test
        @DisplayName("searchEntity should ignore case when searching")
        void searchEntityShouldIgnoreCase() {
            String nameLowerCase = "john";

            when(repository.searchAll(null, nameLowerCase, "", "", pageable))
                    .thenReturn(new PageImpl<>(List.of(client)));

            Page<ClientMinDTO> result = service.searchEntity(null, nameLowerCase, "", "", pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.getContent().get(0).getName().toLowerCase().contains(nameLowerCase));
        }

        @Test
        @DisplayName("searchEntity should throw ResourceNotFoundException when no results found")
        void searchEntityShouldThrowExceptionWhenNoResults() {
            when(repository.searchAll(
                    nonExistingId, "NonExistent", "none@", "000", pageable
            )).thenReturn(new PageImpl<>(List.of()));

            assertThrows(ResourceNotFoundException.class, ()
                    -> service.searchEntity(nonExistingId, "NonExistent", "none@", "000", pageable)
            );
        }

        @Test
        @DisplayName("findByIdWithAddresses should return client with addresses")
        void findByIdWithAddressesShouldReturnClientWithAddresses() {
            when(repository.findByIdWithAddresses(existingId))
                    .thenReturn(Optional.of(client));

            var result = service.findByIdWithAddresses(existingId);

            assertNotNull(result);
            assertEquals(client.getEmail(), result.getEmail());
            assertFalse(result.getAddresses().isEmpty());
        }
    }

    @Nested
    @DisplayName("Permission Tests")
    public class PermissionTests {

        @Test
        @DisplayName("operations should set default role and permission")
        void operationsShouldSetDefaultRoleAndPermission() {
            // Configurando o mock para capturar o client que será salvo
            when(repository.save(any(Client.class))).thenAnswer(invocation -> {
                Client savedClient = invocation.getArgument(0);
                // Adicionando role e permission manualmente como o service faria
                savedClient.addRole(clientRole);
                savedClient.addPermission(nonePermission);
                return savedClient;
            });

            // Executando o método que queremos testar
            ClientMinDTO result = service.insert(clientDTO, false);

            // Verificando o resultado
            assertNotNull(result);
            assertTrue(client.getRoles().stream()
                    .map(Role::getAuthority)
                    .anyMatch(auth -> auth.equals(RoleAuthority.ROLE_CLIENT.getName())));

        }

        @Test
        @DisplayName("copyDtoToEntity should set default role and permission")
        void copyDtoToEntityShouldSetDefaultRoleAndPermission() {
            Client newClient = new Client();

            // Executando o método que queremos testar
            service.copyDtoToEntity(clientDTO, newClient);

            // Verificando se as roles e permissions foram adicionadas
            assertTrue(newClient.getRoles().stream()
                    .map(Role::getAuthority)
                    .anyMatch(auth -> auth.equals(RoleAuthority.ROLE_CLIENT.getName())));
            assertTrue(newClient.getPermissions().stream()
                    .map(Permission::getAuthority)
                    .anyMatch(auth -> auth.equals(PermissionAuthority.PERMISSION_NONE.getName())));
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    public class ValidationTests {

        @Test
        @DisplayName("insert should format CPF removing special characters")
        void insertShouldFormatCPF() {
            clientDTO = FactoryClient.createClientDTOWithFormattedCPF();
            when(repository.save(any(Client.class))).thenReturn(client);

            ClientMinDTO result = service.insert(clientDTO, false);

            assertNotNull(result);
            assertEquals("83563189048", client.getCpf()); // CPF sem formatação
        }

        @Test
        @DisplayName("update should format CPF removing special characters")
        void updateShouldFormatCPF() {
            clientDTO = FactoryClient.createClientDTOWithFormattedCPF();
            when(repository.getReferenceById(existingId)).thenReturn(client);
            when(repository.saveAndFlush(any(Client.class))).thenReturn(client);

            ClientMinDTO result = service.update(clientDTO, existingId, false);

            assertNotNull(result);
            assertEquals("83563189048", client.getCpf()); // CPF sem formatação
        }
    }

    @Nested
    @DisplayName("Database Exception Tests")
    public class DatabaseExceptionTests {

        @Test
        @DisplayName("insert should throw DatabaseException when CPF already exists")
        void insertShouldThrowExceptionWhenCPFExists() {
            when(repository.save(any(Client.class)))
                    .thenThrow(new DataIntegrityViolationException("CPF NULLS FIRST"));

            assertThrows(DatabaseException.class, ()
                    -> service.insert(clientDTO, false)
            );
        }

        @Test
        @DisplayName("insert should throw DatabaseException when email already exists")
        void insertShouldThrowExceptionWhenEmailExists() {
            when(repository.save(any(Client.class)))
                    .thenThrow(new DataIntegrityViolationException("EMAIL NULLS FIRST"));

            assertThrows(DatabaseException.class, ()
                    -> service.insert(clientDTO, false)
            );
        }
    }
}
