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
import rccommerce.entities.User;
import rccommerce.tests.FactoryUser;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    private Long existingId, nonExistingId, adminId, totalUser;
    private String existsName, existsEmail;
    private String nonExistsEmail;
    private Example<User> exampleUser;
    private ExampleMatcher matcher;
    private Pageable pageable;
    private User user, userExample;

    @BeforeEach
    public void SetUp() throws Exception {
        existingId = 5L;
        adminId = 1L;
        nonExistingId = 100L;
        existsName = "Administrador";
        existsEmail = "admin@gmail.com";
        nonExistsEmail = "richard@gmail.com";
        user = FactoryUser.createUser();
        userExample = FactoryUser.createNewUser();
        totalUser = repository.count();
        pageable = PageRequest.of(0, 10);
        matcher = ExampleMatcher.matching();
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        user.setId(null);

        User result = repository.save(user);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(totalUser + 1L, repository.count());
    }

    @Test
    public void saveShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {
        user.setId(null);
        user.setEmail(existsEmail);

        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(user);
        });
        Assertions.assertNotNull(exception.getMessage());
    }

    @Test
    public void updateShouldUserWhenIdExists() {
        user = repository.getReferenceById(existingId);
        user.setName("Anthony");

        User result = repository.saveAndFlush(user);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("Anthony", result.getName());
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenNonExistsId() {
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            User result = repository.getReferenceById(nonExistingId);
            result.setName("Paul");
            repository.saveAndFlush(result);
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    public void updateShouldThrowDataIntegrityViolationExceptionWhenEmailAlreadyExists() {

        DataIntegrityViolationException exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            User result = repository.getReferenceById(existingId);
            result.setEmail(existsEmail);
            repository.saveAndFlush(result);
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    public void findByIdShouldOptionalUserWhenExixtID() {
        Optional<User> result = repository.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.get().getId());
    }

    @Test
    public void findByIdShouldObjectEmptyWhenNonExixtId() {
        Optional<User> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void searchUserAndRolesByEmailShouldLoggedUserWhenExistUser() {
        Optional<User> result = repository.searchUserRolesAndPermissionsByEmail(existsEmail);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existsEmail, result.get().getEmail());
    }

    @Test
    public void searchUserAndRolesByEmailShouObjectEmptyWhenNonExistUser() {
        Optional<User> result = repository.searchUserRolesAndPermissionsByEmail(nonExistsEmail);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findByShouldReturnPageUsersWhenNullIdEmptyNameAndEmailAndCpf() {
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email");

        exampleUser = Example.of(userExample, matcher);

        Page<User> result = repository.findBy(exampleUser, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals((long) totalUser, result.getContent().size());
    }

    @Test
    public void findByShouldReturnUserWhenExistsIdEmptyNameAndEmail() {
        userExample.setId(adminId);
        matcher.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("nameUnaccented")
                .withIgnorePaths("email");

        exampleUser = Example.of(userExample, matcher);

        Page<User> result = repository.findBy(exampleUser, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals((long) adminId, result.getContent().size());
        Assertions.assertEquals(existsName, result.getContent().get(0).getName());
    }

    @Test
    public void findByShouldReturnUserWhenExistsNameAndNullIdEmptyEmail() {
        userExample.setNameUnaccented(existsName);
        matcher.withIgnorePaths("id")
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("email");
        exampleUser = Example.of(userExample, matcher);

        Page<User> result = repository.findBy(exampleUser, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsName, result.getContent().get(0).getName());
        Assertions.assertEquals(adminId, result.getContent().get(0).getId());
    }

    @Test
    public void findByShouldReturnUserWhenExistsEmailAndNullIdEmptyName() {
        userExample.setEmail(existsEmail);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        exampleUser = Example.of(userExample, matcher);

        Page<User> result = repository.findBy(exampleUser, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(existsEmail, result.getContent().get(0).getEmail());
    }

    @Test
    public void findByShouldReturnPageEmptyWhenInvalidAnyParameter() {
        userExample.setEmail(nonExistsEmail);
        matcher.withIgnorePaths("id")
                .withIgnorePaths("nameUnaccented")
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        exampleUser = Example.of(userExample, matcher);

        Page<User> result = repository.findBy(exampleUser, query -> query.page(pageable));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getContent().size());
    }
}
