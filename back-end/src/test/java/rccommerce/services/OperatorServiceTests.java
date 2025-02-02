package rccommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
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

    @BeforeEach
    public void setUp() throws Exception {
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
        when(repository.save(ArgumentMatchers.any())).thenReturn(operator);
        when(repository.saveAndFlush(ArgumentMatchers.any())).thenReturn(operator);
        when(repository.existsById(existsId)).thenReturn(true);
        when(repository.existsById(dependentId)).thenReturn(true);
        when(repository.existsById(nonExistsId)).thenReturn(false);

        serviceSpy = Mockito.spy(service);
        doNothing().when(serviceSpy).copyDtoToEntity(any(), any());
        doNothing().when(serviceSpy).checkUserPermissions(any());
        doNothing().when(serviceSpy).checkUserPermissions(any(), any());
        when(messageSource.getMessage(any(String.class), any(), any())).thenReturn("Operador");
    }

    @Test
    public void searchEntityShouldReturnPagedOperatorMinDTO() {
        when(repository.findBy(any(), any())).thenReturn(new PageImpl<>(List.of(operator)));

        Page<OperatorMinDTO> result = service.searchEntity(operator.getId(), operator.getName(), operator.getEmail(), pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getSize());
        Assertions.assertEquals(operator.getName(), result.toList().get(0).getName());
        Assertions.assertEquals(operator.getEmail(), result.toList().get(0).getEmail());
    }

    @Test
    public void searchEntityShouldThrowResourceNotFoundExceptionWhenNonExixtsOperator() {
        when(repository.findBy(any(), any())).thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.searchEntity(nonExistsId, "", "", pageable);
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

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
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
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).save(ArgumentMatchers.any());

        Assertions.assertThrows(DatabaseException.class, () -> {
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

        Assertions.assertThrows(DatabaseException.class, () -> {
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

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            serviceSpy.delete(nonExistsId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> {
            serviceSpy.delete(dependentId);
        });
    }

}
