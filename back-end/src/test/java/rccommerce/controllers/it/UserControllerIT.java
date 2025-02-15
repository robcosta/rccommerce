package rccommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.entities.User;
import rccommerce.tests.FactoryUser;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    private String adminToken, invalidToken;
    private String userAdminEmail, userAdminPassword;
    private String userName, userEmail;

    private long existingId, nonExistingId;

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        userAdminEmail = "admin@gmail.com";
        userAdminPassword = "123456";

        existingId = 3L;
        nonExistingId = 100L;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
        invalidToken = adminToken + "xpto";

        user = FactoryUser.createUser();
        user.addRole(FactoryUser.createRoleAdmin());
        user.setId(null);
    }

    @Test
    public void getMeShouldReturnUserLoggedWhenValidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(1L));
        resultActions.andExpect(jsonPath("$.name").value("Administrador"));
        resultActions.andExpect(jsonPath("$.email").value("admin@gmail.com"));
        resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    public void getMeShouldUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllShouldREturnPageWhenValidTokenAndNameAndEmailParamtersAreEmpty() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(8L));
        resultActions.andExpect(jsonPath("$.content[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.content[0].name").value("Administrador"));
        resultActions.andExpect(jsonPath("$.content[0].email").value("admin@gmail.com"));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_ADMIN"));

    }

    @Test
    public void findAllShouldReturnPageWhenValidTokenAndNameParamIsNotEmptyEmailIsEmpty() throws Exception {
        userName = "Alex Blue";
        ResultActions resultActions = mockMvc.perform(get("/users?name={userName}", userName)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content[0].id").value(3L));
        resultActions.andExpect(jsonPath("$.content[0].name").value("Alex Blue"));
        resultActions.andExpect(jsonPath("$.content[0].email").value("alex@gmail.com"));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_SELLER"));

    }

    @Test
    public void findAllShouldReturnPageWhenValidTokenAndEmailParamIsNotEmptyNameParamIsEmpty() throws Exception {
        userEmail = "alex@gmail.com";
        ResultActions resultActions = mockMvc.perform(get("/users?email={email}", userEmail)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content[0].id").value(3L));
        resultActions.andExpect(jsonPath("$.content[0].name").value("Alex Blue"));
        resultActions.andExpect(jsonPath("$.content[0].email").value("alex@gmail.com"));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_SELLER"));

    }

    @Test
    public void findAllShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShoulReturUserMinDTOWhenValidTokenAndExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value("Alex Blue"));
        resultActions.andExpect(jsonPath("$.email").value("alex@gmail.com"));
        resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_SELLER"));
    }

    @Test
    public void findByIdShoulReturNotFoundWhenValidTokenAndDoesNotExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShoulReturUnaauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/users/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }
}
