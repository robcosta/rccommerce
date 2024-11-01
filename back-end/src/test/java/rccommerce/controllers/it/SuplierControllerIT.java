package rccommerce.controllers.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import rccommerce.dto.SuplierDTO;
import rccommerce.entities.Suplier;
import rccommerce.tests.FactorySuplier;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SuplierControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String adminToken, invalidToken;
    private String userAdminEmail, userAdminPassword;
    private String existsSuplierName;
    private String nonExistsSuplierName;

    private long existingId, nonExistingId, existingUpdateId;
    private Integer countSuplier;
    private Suplier suplier;
    private SuplierDTO suplierDTO;

    @BeforeEach
    void setUp() throws Exception {
        userAdminEmail = "admin@gmail.com";
        userAdminPassword = "123456";

        existsSuplierName = "Diversos";

        nonExistsSuplierName = "Outro";

        existingId = 1L;
        existingUpdateId = 3L;
        nonExistingId = 100L;
        countSuplier = 6;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
        invalidToken = adminToken + "xpto";

        suplier = FactorySuplier.createSuplier();
    }

    @Test
    public void findAllShouldREturnPageWhenValidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/all?sort=id")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(countSuplier));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsSuplierName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenParamEmpty() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/search?id={id}&name={name}", "", "")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(countSuplier));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsSuplierName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndIdParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/search?id={id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsSuplierName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndNameParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/search?name={name}", existsSuplierName)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsSuplierName));

    }

    @Test
    public void searchEntityShouldReturnNotFoundWhenIdParamDoesNotExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/search?name={name}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void searchEntityShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/search")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShoulReturSuplierMinDTOWhenValidTokenAndExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value(existsSuplierName));
    }

    @Test
    public void findByIdShoulReturNotFoundWhenValidTokenAndDoesNotExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShoulReturUnaauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/supliers/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnSuplierMinDTOWhenAllDataIsValid() throws Exception {
        suplierDTO = FactorySuplier.createSuplierDTO();
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        String expectedName = suplier.getName();

        ResultActions resultActions = mockMvc.perform(post("/supliers")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    public void insertShouldReturnBadRequestWhenDataIsValidAndNameDoesNotUnique() throws Exception {
        suplier.setName(existsSuplierName);
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        ResultActions resultActions = mockMvc.perform(post("/supliers")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.error").value("Nome informado já existe"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidName() throws Exception {
        suplier.setName("Ro");
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        ResultActions resultActions = mockMvc.perform(post("/supliers")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
        resultActions.andExpect(jsonPath("$.error").value("Dados inválidos"));
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenExistsName() throws Exception {
        suplier.setName(existsSuplierName);
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        ResultActions resultActions = mockMvc.perform(post("/supliers")
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnSuplierMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        String expectedName = suplier.getName();

        ResultActions resultActions = mockMvc.perform(put("/supliers/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingUpdateId));
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenInvalidTokenAndAllDataIsValid() throws Exception {
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        ResultActions resultActions = mockMvc.perform(put("/supliers/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnBadRequestWhenDataIsValidAndNameDoesNotUnique() throws Exception {
        suplier.setName(existsSuplierName);
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        ResultActions resultActions = mockMvc.perform(put("/supliers/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
        suplier.setName("Co");
        suplierDTO = FactorySuplier.createSuplierDTO(suplier);
        String jsonBody = objectMapper.writeValueAsString(suplierDTO);

        ResultActions resultActions = mockMvc.perform(put("/supliers/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteShouldNoContentWhenAdminLoggedAndExistsId() throws Exception {

        ResultActions result = mockMvc.perform(delete("/supliers/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions result = mockMvc.perform(delete("/supliers/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldNotFoundWhenAdminLoggedAndDoesNotExisitsId() throws Exception {

        ResultActions result = mockMvc.perform(delete("/supliers/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
