package rccommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Operator;
import rccommerce.repositories.OperatorRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.tests.FactoryUser;

@ExtendWith(SpringExtension.class)
public class OperatorServiceTests {

    @InjectMocks
    private OperatorService service;

    @Mock
    private OperatorRepository repository;

    @Mock
    private MessageSource messageSource;

    private Long existsId, nonExistsId, dependentId;
    private Operator operator;
    private Pageable pageable;
    private OperatorService serviceSpy;
    private OperatorDTO dto;
    private UserMinDTO adminDTO, operatorDTO;

    private ResourceNotFoundException assertNotFound;
    private DatabaseException assertDatabase;

    @BeforeEach
    void setUp() throws Exception {
        operator = FactoryUser.createOperatorAdmin();
        dto = FactoryUser.createOperatorDTO(operator);

        adminDTO = FactoryUser.createUserMinDTO();
        adminDTO.getRoles().clear();
        adminDTO.getRoles().add("ROLE_ADMIN");

        operatorDTO = FactoryUser.createUserMinDTO();
        operatorDTO.getRoles().clear();
        operatorDTO.getRoles().add("ROLE_OPERATOR");

        pageable = PageRequest.of(0, 10);
    
        existsId = 7L;
        nonExistsId = 100L;
        dependentId = 3L;

        when(repository.getReferenceById(existsId)).thenReturn(operator);
        when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(operator);
        when(repository.existsById(existsId)).thenReturn(true);
        when(repository.existsById(dependentId)).thenReturn(true);
        when(repository.existsById(nonExistsId)).thenReturn(false);

        // Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        serviceSpy = Mockito.spy(service);
        doNothing().when(serviceSpy).copyDtoToEntity(any(), any());
        doNothing().when(serviceSpy).checkUserPermissions(any(), any(), any());
        when(messageSource.getMessage(any(String.class), any(), any())).thenReturn("Operador");
    }

    @Test
    public void searchAllShouldReturnPagedOperatorMinDTO() {
        Example<Operator> example = Example.of(operator);

        when(repository.findAll(eq(example), eq(pageable))).thenReturn(new PageImpl<>(List.of(operator, operator, operator)));

        Page<OperatorMinDTO> result = serviceSpy.searchAll(example, pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getSize());
        Assertions.assertEquals(operator.getName(), result.toList().get(0).getName());
        Assertions.assertEquals(operator.getEmail(), result.toList().get(1).getEmail());
    }

    @Test
    public void searchAllShouldThrowResourceNotFoundExceptionWhenNonExixtsOperator() {
        Example<Operator> example = Example.of(operator);

        when(repository.findAll(eq(example), eq(pageable))).thenReturn(Page.empty());

        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.searchAll(example, pageable);
        });
    }

    @Test
    public void findByIdShouldReturnOperatorMinDTOWhenExistsId() {
        Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(operator));

        OperatorMinDTO result = serviceSpy.findById(existsId);

        Assertions.assertEquals(result.getId(), existsId);
        Assertions.assertEquals(result.getName(), operator.getName());
        Assertions.assertEquals(result.getEmail(), operator.getEmail());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
        Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());

        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.findById(nonExistsId);
        });
    }

    @Test
    public void insertShouldReturnOperatorMinDTOWhenEmailIsUnique() {
        OperatorMinDTO result = serviceSpy.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(operator.getName(), result.getName());
        Assertions.assertEquals(operator.getEmail(), result.getEmail());
    }

    @Test
    public void insertShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertDatabase = Assertions.assertThrows(DatabaseException.class, () -> {
            serviceSpy.insert(dto);
        });
    }

    @Test
    public void updateShouldReturnOperatorMinDTOWhenExistsIdAndIdAndEmailIsUnique() {
        OperatorMinDTO result = serviceSpy.update(dto, existsId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existsId, result.getId());
        Assertions.assertEquals(operator.getName(), result.getName());
    }

    @Test
    public void updateShouldTrowDatabaseExceptionWhenEamilDoesNotUnique() {
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).saveAndFlush(ArgumentMatchers.any());

        assertDatabase = Assertions.assertThrows(DatabaseException.class, () -> {
            serviceSpy.update(dto, existsId);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExistsAndIdDoesNotDependent() {
        doNothing().when(repository).deleteById(existsId);

        Assertions.assertDoesNotThrow(() -> {
            serviceSpy.delete(existsId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        assertNotFound = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.delete(nonExistsId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        assertDatabase = Assertions.assertThrows(DatabaseException.class, () -> {
            serviceSpy.delete(dependentId);
        });
    }

    // @Test
    // public void copyDtoToEntityShouldCopyAllDataDtoForOperator(){
    // 	dto.getAuths().clear();
    // 	dto.getAuths().add("CREATE");
    // 	dto.getRoles().clear();
    // 	dto.getRoles().add("ROLE_SELLER");
    // 	Mockito.when(userService.getMe()).thenReturn(adminDTO);
    // 	Mockito.when(roleRepository.findByAuthority(ArgumentMatchers.anyString())).thenReturn(new Role(null, "ROLE_SELLER"));
    // 	Mockito.when(authRepository.findByAuth(ArgumentMatchers.anyString())).thenReturn(new Verify(null, "CREATE"));
    // 	service.copyDtoToEntity(dto, operator);
    // 	List<String> resultAuhts = operator.getAuths().stream().map(x -> x.getAuth()).toList();
    // 	List<String> resultRoles = operator.getRoles().stream().map(x -> x.getAuthority()).toList();
    // 	Assertions.assertEquals(dto.getName(), operator.getName());
    // 	Assertions.assertEquals(dto.getEmail(), operator.getEmail());
    // 	Assertions.assertEquals(dto.getCommission(), operator.getCommission());
    // 	Assertions.assertTrue(resultAuhts.contains("CREATE"));
    // 	Assertions.assertTrue(resultRoles.contains("ROLE_SELLER"));
    // }
    // @Test
    // public void copyDtoToEntityShouldCopyAllDataDtoForOperatorWhenEmptyPassword(){
    // 	dto.getAuths().clear();
    // 	dto.getAuths().add("CREATE");
    // 	dto.getRoles().clear();
    // 	dto.getRoles().add("ROLE_SELLER");
    // 	dto.getPassword().isEmpty();
    // 	Mockito.when(userService.getMe()).thenReturn(adminDTO);
    // 	Mockito.when(roleRepository.findByAuthority(ArgumentMatchers.anyString())).thenReturn(new Role(null, "ROLE_SELLER"));
    // 	Mockito.when(authRepository.findByAuth(ArgumentMatchers.anyString())).thenReturn(new Verify(null, "CREATE"));
    // 	service.copyDtoToEntity(dto, operator);
    // 	List<String> resultAuhts = operator.getAuths().stream().map(x -> x.getAuth()).toList();
    // 	List<String> resultRoles = operator.getRoles().stream().map(x -> x.getAuthority()).toList();
    // 	Assertions.assertEquals(dto.getName(), operator.getName());
    // 	Assertions.assertEquals(dto.getEmail(), operator.getEmail());
    // 	Assertions.assertEquals(dto.getCommission(), operator.getCommission());
    // 	Assertions.assertTrue(resultAuhts.contains("CREATE"));
    // 	Assertions.assertTrue(resultRoles.contains("ROLE_SELLER"));
    // }
    // @Test
    // public void copyDtoToEntityShouldCopyDataDtoForOperatorMinusRolesAndAuthWhenUserLoggerDoesNotAdmin(){
    // 	dto.getAuths().clear();
    // 	dto.getAuths().add("CREATE");
    // 	dto.getRoles().clear();
    // 	dto.getRoles().add("ROLE_SELLER");
    // 	Mockito.when(userService.getMe()).thenReturn(operatorDTO);
    // 	service.copyDtoToEntity(dto, operator);
    // 	List<String> resultAuhts = operator.getAuths().stream().map(x -> x.getAuth()).toList();
    // 	List<String> resultRoles = operator.getRoles().stream().map(x -> x.getAuthority()).toList();
    // 	Assertions.assertEquals(dto.getName(), operator.getName());
    // 	Assertions.assertEquals(dto.getEmail(), operator.getEmail());
    // 	Assertions.assertEquals(dto.getCommission(), operator.getCommission());
    // 	Assertions.assertFalse(resultAuhts.contains("CREATE"));
    // 	Assertions.assertFalse(resultRoles.contains("ROLE_SELLER"));
    // }
    // @Test
    // public void copyDtoToEntityShouldThowInvalidArgumentExecptionWhenAuthDoesNotExists(){
    // 	dto.getAuths().clear();
    // 	dto.getAuths().add("INEXISTENTE");
    // 	dto.getRoles().clear();
    // 	dto.getRoles().add("ROLE_SELLER");
    // 	Mockito.when(userService.getMe()).thenReturn(adminDTO);		
    // 	Assertions.assertThrows(InvalidArgumentExecption.class, () -> {
    // 		service.copyDtoToEntity(dto, operator);
    // 	});
    // }
    // @Test
    // public void copyDtoToEntityShouldThowInvalidArgumentExecptionWhenRoleDoesNotExists(){
    // 	dto.getAuths().clear();
    // 	dto.getAuths().add("ALL");
    // 	dto.getRoles().clear();
    // 	dto.getRoles().add("INEXISTENTE");
    // 	Mockito.when(userService.getMe()).thenReturn(adminDTO);
    // 	Assertions.assertThrows(InvalidArgumentExecption.class, () -> {
    // 		service.copyDtoToEntity(dto, operator);
    // 	});
    // }
    // @Test
    // public void copyDtoToEntityShouldThowInvalidArgumentExecptionWhenRoleIsEmpty(){
    // 	dto.getAuths().clear();
    // 	dto.getAuths().add("ALL");
    // 	dto.getRoles().clear();	
    // 	Mockito.when(userService.getMe()).thenReturn(adminDTO);
    // 	Assertions.assertThrows(InvalidArgumentExecption.class, () -> {
    // 		service.copyDtoToEntity(dto, operator);
    // 	});
    // }
}
