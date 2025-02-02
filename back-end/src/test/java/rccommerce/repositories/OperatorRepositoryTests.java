package rccommerce.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.entities.Operator;
import rccommerce.tests.FactoryUser;

@DataJpaTest
public class OperatorRepositoryTests {

    @Autowired
    private OperatorRepository repository;

    private Long existingId, nonExistingId, adminId;
    private String existsName, existsEmail;
    private String nonExistsEmail;
    private Operator operator, operatorExample;
    private Long totalOperator;
    private Example<Operator> exampleOperator;
    private ExampleMatcher matcher;
    private Pageable pageable;

    @BeforeEach
    void SetUp() throws Exception {
        existingId = 3L;
        adminId = 1L;
        nonExistingId = 100L;
        existsName = "Administrador";
        existsEmail = "admin@gmail.com";
        nonExistsEmail = "richard@gmail.com";
        operator = FactoryUser.createOperatorAdmin();
        operatorExample = FactoryUser.createNewOperator();
        totalOperator = repository.count();
        pageable = PageRequest.of(0, 10);
        matcher = ExampleMatcher.matching();
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(7l);

        Optional<Operator> result = repository.findById(7l);

        Assertions.assertFalse(result.isPresent());
        Assertions.assertEquals(totalOperator, repository.count());
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        operator.setId(null);

        Operator result = repository.save(operator);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(totalOperator + 1L, repository.count());
    }

    @Test
    public void saveShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
        operator.setId(null);
        operator.setEmail(existsEmail);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(operator);
        });
    }

    @Test
    public void updateShouldUpdateOperatorWhenIdExists() {
        operator = repository.getReferenceById(existingId);
        operator.setName("Anthony");

        Operator result = repository.saveAndFlush(operator);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("Anthony", result.getName());
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenNonExistsId() {

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            Operator result = repository.getReferenceById(nonExistingId);
            result.setName("Paul");
            repository.saveAndFlush(result);
        });
    }

    @Test
    public void updateShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Operator result = repository.getReferenceById(existingId);
            result.setEmail(existsEmail);
            repository.saveAndFlush(result);
        });
    }

    @Test
    public void findByIdShouldOptionalOperatorWhenExixtID() {
        Optional<Operator> result = repository.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.get().getId());
    }

    @Test
    public void findByIdShouldObjectEmptyWhenNonExixtId() {
        Optional<Operator> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findByShouldReturnPageOperatorsWhenNullIdEmptyNameAndEmailAndCpf() {
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email");

        exampleOperator = Example.of(operatorExample, matcher);

        Page<Operator> result = repository.findBy(exampleOperator, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(totalOperator, result.getContent().size());
    }

    @Test
    public void findByShouldReturnOperatorWhenExistsIdEmptyNameAndEmail() {
        operatorExample.setId(adminId);
        matcher.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email");

        exampleOperator = Example.of(operatorExample, matcher);

        Page<Operator> result = repository.findBy(exampleOperator, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(adminId, result.getContent().size());
        Assertions.assertEquals(existsName, result.getContent().get(0).getName());
    }

    @Test
    public void findByShouldReturnOperatorWhenExistsNameAndNullIdEmptyEmail() {
        operatorExample.setNameUnaccented(existsName);
        matcher.withIgnorePaths("id")
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("email");
        exampleOperator = Example.of(operatorExample, matcher);

        Page<Operator> result = repository.findBy(exampleOperator, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsName, result.getContent().get(0).getName());
        Assertions.assertEquals(adminId, result.getContent().get(0).getId());
    }

    @Test
    public void findByShouldReturnOperatorWhenExistsEmailAndNullIdEmptyName() {
        operatorExample.setEmail(existsEmail);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        exampleOperator = Example.of(operatorExample, matcher);

        Page<Operator> result = repository.findBy(exampleOperator, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
    }

    @Test
    public void findByShouldReturnPageEmptyWhenInvalidAnyParameter() {
        operatorExample.setEmail(nonExistsEmail);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        exampleOperator = Example.of(operatorExample, matcher);

        Page<Operator> result = repository.findBy(exampleOperator, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getContent().size());
    }
}
