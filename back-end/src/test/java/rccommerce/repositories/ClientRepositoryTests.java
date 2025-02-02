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
import rccommerce.entities.Client;
import rccommerce.tests.FactoryUser;

@DataJpaTest
public class ClientRepositoryTests {

    @Autowired
    private ClientRepository repository;

    private long existingId, nonExistingId;
    private String existsName, existsEmail, existsCpf;
    private String nonExistsCPF;
    private Client client, clientExample;
    private long totalClient;
    private Example<Client> exampleClient;
    private ExampleMatcher matcher;
    private Pageable pageable;

    @BeforeEach
    void SetUp() throws Exception {
        existingId = 5L;
        nonExistingId = 100L;
        existsName = "Maria Yellow";
        existsEmail = "maria@gmail.com";
        existsCpf = "46311990083";
        nonExistsCPF = "00000000000";
        client = FactoryUser.createClient();
        clientExample = FactoryUser.createNewClient();
        pageable = PageRequest.of(0, 10);
        totalClient = repository.count();
        matcher = ExampleMatcher.matching();
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(8L);

        Optional<Client> result = repository.findById(8L);

        Assertions.assertFalse(result.isPresent());
        Assertions.assertEquals(totalClient, repository.count());
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        client.setId(null);

        Client result = repository.save(client);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(totalClient + 1L, repository.count());
    }

    @Test
    public void saveShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
        client.setId(null);
        client.setEmail(existsEmail);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(client);
        });
    }

    @Test
    public void updateShouldUpdateClientWhenIdExists() {
        client.setId(existingId);
        client.setName("Anthony");

        Client result = repository.saveAndFlush(client);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("Anthony", result.getName());
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenNonExistsId() {

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            Client result = repository.getReferenceById(nonExistingId);
            result.setName("Paul");
            repository.saveAndFlush(result);
        });
    }

    @Test
    public void updateShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
        Client result = repository.getReferenceById(6L);
        result.setEmail(existsEmail);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(result);
        });
    }

    @Test
    public void findByIdShouldOptionalClientWhenExixtID() {
        Optional<Client> result = repository.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.get().getId());
    }

    @Test
    public void findByIdShouldObjectEmptyWhenNonExixtId() {
        Optional<Client> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findByShouldReturnPageClientsWhenNullIdEmptyNameAndEmailAndCpf() {
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email")
                .withIgnorePaths("cpf");
        exampleClient = Example.of(clientExample, matcher);

        Page<Client> result = repository.findBy(exampleClient, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(totalClient, result.getContent().size());
    }

    @Test
    public void findByShouldReturnClientWhenExistsIdEmptyNameEmailAndCpf() {
        clientExample.setId(existingId);
        matcher.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email")
                .withIgnorePaths("cpf");
        exampleClient = Example.of(clientExample, matcher);

        Page<Client> result = repository.findBy(exampleClient, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsName, result.getContent().get(0).getName());
    }

    @Test
    public void findByShouldReturnClientWhenExistsNameAndNullIdEmptyEmailAndCpf() {
        clientExample.setNameUnaccented(existsName);
        matcher.withIgnorePaths("id")
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("email")
                .withIgnorePaths("cpf");
        exampleClient = Example.of(clientExample, matcher);

        Page<Client> result = repository.findBy(exampleClient, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsName, result.getContent().get(0).getName());
    }

    @Test
    public void findByShouldReturnClientWhenExistsEmailAndNullIdEmptyNameAndCpf() {
        clientExample.setEmail(existsEmail);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("cpf");
        exampleClient = Example.of(clientExample, matcher);

        Page<Client> result = repository.findBy(exampleClient, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
    }

    @Test
    public void findByShouldReturnClientWhenExistsCpfAndNullIdEmptyNameAndEmail() {
        clientExample.setCpf(existsCpf);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email")
                .withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        exampleClient = Example.of(clientExample, matcher);

        Page<Client> result = repository.findBy(exampleClient, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsCpf, result.getContent().get(0).getCpf());
    }

    @Test
    public void findByShouldReturnPageEmptyWhenInvalidAnyParameter() {
        clientExample.setCpf(nonExistsCPF);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email")
                .withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        exampleClient = Example.of(clientExample, matcher);

        Page<Client> result = repository.findBy(exampleClient, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getContent().size());
    }
}
