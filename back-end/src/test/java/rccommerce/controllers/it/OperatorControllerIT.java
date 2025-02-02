package rccommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import rccommerce.dto.OperatorDTO;
import rccommerce.entities.Operator;
import rccommerce.tests.FactoryUser;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OperatorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String adminToken, sellerToken, invalidToken, roleAdmin, roleSeller;
    private String userAdminEmail, userAdminPassword, nameAdmin, userSellerEmail, userSellerPassword;
    private String existsOperatorName, existsOperatorEmail, nonExistsOperatorName, nonExistsOperatorEmail, emailUnique;

    private long existingId, nonExistingId;
    private Integer sizeOperator;
    private Operator operator;
    private OperatorDTO operatorDTO;

    @BeforeEach
    public void setUp() throws Exception {
        userAdminEmail = "admin@gmail.com";
        userAdminPassword = "123456";
        userSellerEmail = "alex@gmail.com";
        userSellerPassword = "123456";

        existsOperatorName = "Alex Blue";
        existsOperatorEmail = "alex@gmail.com";

        nonExistsOperatorName = "Peter Black";
        nonExistsOperatorEmail = "peter@gmail.com";

        emailUnique = "bob@gmail.com";

        existingId = 3L;
        roleSeller = "ROLE_SELLER";
        nonExistingId = 100L;
        nameAdmin = "Administrador";
        roleAdmin = "ROLE_ADMIN";

        sizeOperator = 4;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
        sellerToken = tokenUtil.obtainAccessToken(mockMvc, userSellerEmail, userSellerPassword);
        invalidToken = adminToken + "xpto";

        operator = FactoryUser.createOperatorAdmin();
        operator.addPermission(FactoryUser.createPermissionAll());
        operator.addRole(FactoryUser.createRoleAdmin());
        operator.setId(null);
    }

    @Test
    public void findAllShouldREturnPageWhenValidTokenAndEmptyParams() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/all")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(sizeOperator));
        resultActions.andExpect(jsonPath("$.content[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.content[0].name").value(nameAdmin));
        resultActions.andExpect(jsonPath("$.content[0].email").value(userAdminEmail));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value(roleAdmin));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenParamEmpty() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?id={id}&name={name}&email={email}", "", "", "")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.totalElements").value(sizeOperator));
        resultActions.andExpect(jsonPath("$.content[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.content[0].name").value(nameAdmin));
        resultActions.andExpect(jsonPath("$.content[0].email").value(userAdminEmail));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value(roleAdmin));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndIdParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?id={id}", 1L)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.content[0].name").value(nameAdmin));
        resultActions.andExpect(jsonPath("$.content[0].email").value(userAdminEmail));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value(roleAdmin));

    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndNameParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?name={name}", existsOperatorName)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content[0].id").value(3L));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsOperatorName));
        resultActions.andExpect(jsonPath("$.content[0].email").value(existsOperatorEmail));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value(roleSeller));

    }

    @Test
    public void fsearchEntityShouldReturnPageWhenValidTokenAndEmailParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?email={email}", existsOperatorEmail)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content[0].id").value(3L));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsOperatorName));
        resultActions.andExpect(jsonPath("$.content[0].email").value(existsOperatorEmail));
        resultActions.andExpect(jsonPath("$.content[0].roles[0]").value(roleSeller));

    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndNameParamDoesNotExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?name={name}", nonExistsOperatorName)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());

    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndEmailParamDoesNotExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?email={email}", nonExistsOperatorEmail)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());

    }

    @Test
    public void searchEntityShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/search?name={existsOperatorName}", existsOperatorName)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShoulReturOperatorMinDTOWhenValidTokenAndExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value(existsOperatorName));
        resultActions.andExpect(jsonPath("$.email").value(existsOperatorEmail));
        resultActions.andExpect(jsonPath("$.roles[0]").value(roleSeller));
    }

    @Test
    public void findByIdShoulReturNotFoundWhenValidTokenAndDoesNotExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShoulReturUnaauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnOperatorMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
        operatorDTO = FactoryUser.createOperatorDTO(operator);

        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        String expectedName = operator.getName();
        String expectedEmail = operator.getEmail();

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
        resultActions.andExpect(jsonPath("$.email").value(expectedEmail));
        resultActions.andExpect(jsonPath("$.roles[0]").value(roleAdmin));
    }

    @Test
    public void insertShouldReturnForbiddenWhenOperatorLoggedAndAllDataIsValid() throws Exception {
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + sellerToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenOperatorLoggedAndAllDataIsValid() throws Exception {
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnBadRequestWhenOperatorLoggedAndAllDataIsValidAndEmailDoesNotUnique() throws Exception {
        operator.setEmail(emailUnique);
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
        operator.setName("Ro");
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidEmail() throws Exception {
        operator.setEmail("roberto.com");
        operator.addRole(FactoryUser.createRoleOperator());
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidCommission() throws Exception {
        operator.setCommission(new BigDecimal(-1.0));

        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidPassword() throws Exception {
        operator.setPassword("12A34B");
        operator.addRole(FactoryUser.createRoleOperator());
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidRoles() throws Exception {
        operator.getRoles().clear();
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(post("/operators")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnOperatorMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        String expectedName = operator.getName();
        String expectedEmail = operator.getEmail();

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
        resultActions.andExpect(jsonPath("$.email").value(expectedEmail));
        resultActions.andExpect(jsonPath("$.roles[0]").value(roleAdmin));
    }

    // @Test
    // public void updateShouldReturnForbiddenWhenOperatorLoggedAndDataIsValid() throws Exception {
    // 	operatorDTO = FactoryUser.createOperatorDTO(operator);
    // 	String jsonBody = objectMapper.writeValueAsString(operatorDTO);
    // 	ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
    // 			.header("Authorization", "Bearer " + sellerToken)
    // 			.content(jsonBody)
    // 			.contentType(MediaType.APPLICATION_JSON)
    // 			.accept(MediaType.APPLICATION_JSON));
    // 	resultActions.andExpect(status().isForbidden());
    // }
    @Test
    public void updateShouldReturnUnauthorizedWhenInvalidTokenAndAllDataIsValid() throws Exception {
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnBadRequestWhenOperatorLoggedAndAllDataIsValidAndEmailDoesNotUnique() throws Exception {
        operator.setEmail(emailUnique);
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
        operator.setName("");
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidEmail() throws Exception {
        operator.setEmail("roberto@");
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidComission() throws Exception {
        operator.setCommission(new BigDecimal(-1.0));
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updatetShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidPasswor() throws Exception {
        operator.setPassword("95-1");
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidRoles() throws Exception {
        operator.getRoles().clear();
        operatorDTO = FactoryUser.createOperatorDTO(operator);
        String jsonBody = objectMapper.writeValueAsString(operatorDTO);

        ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteShouldNoContentWhenAdminLoggedAndDoNotDeleteYourself() throws Exception {

        ResultActions result
                = mockMvc.perform(delete("/operators/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions result
                = mockMvc.perform(delete("/operators/{id}", existingId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldNotFoundWhenAdminLoggedAndDoesNotExisitsId() throws Exception {

        ResultActions result
                = mockMvc.perform(delete("/operators/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
