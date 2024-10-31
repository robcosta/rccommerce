package rccommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import rccommerce.dto.CategoryDTO;
import rccommerce.entities.Category;
import rccommerce.tests.FactoryCategory;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String adminToken, invalidToken;
    private String userAdminEmail, userAdminPassword;
    private String existsCategoryName;
    private String nonExistsCategoryName;

    private long existingId, nonExistingId, existingUpdateId;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() throws Exception {
        userAdminEmail = "admin@gmail.com";
        userAdminPassword = "123456";

        existsCategoryName = "LIVROS";

        nonExistsCategoryName = "MESA";

        existingId = 1L;
        existingUpdateId = 3L;
        nonExistingId = 100L;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
        invalidToken = adminToken + "xpto";

        category = FactoryCategory.createCategory();
    }

    @Test
    public void findAllShouldREturnPageWhenValidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/all?sort=id")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(3));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsCategoryName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenParamEmpty() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/search?id={id}&name={name}", "", "")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(3));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsCategoryName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndIdParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/search?id={id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsCategoryName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndNameParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/search?name={name}", existsCategoryName)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsCategoryName));

    }

    @Test
    public void searchEntityShouldReturnNotFoundWhenIdParamDoesNotExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/search?name={name}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void searchEntityShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/search")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShoulReturCategoryMinDTOWhenValidTokenAndExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value(existsCategoryName));
    }

    @Test
    public void findByIdShoulReturNotFoundWhenValidTokenAndDoesNotExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShoulReturUnaauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/categories/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnCategoryMinDTOWhenAllDataIsValid() throws Exception {
        categoryDTO = FactoryCategory.createCategoryDTO();
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        String expectedName = category.getName();

        ResultActions resultActions = mockMvc.perform(post("/categories")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    public void insertShouldReturnBadRequestWhenDataIsValidAndNameDoesNotUnique() throws Exception {
        category.setName(existsCategoryName);
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions resultActions = mockMvc.perform(post("/categories")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.error").value("Nome informado já existe"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidName() throws Exception {
        category.setName("Ro");
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions resultActions = mockMvc.perform(post("/categories")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
        resultActions.andExpect(jsonPath("$.error").value("Dados inválidos"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidName2() throws Exception {
        category.setName("MESA");
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions resultActions = mockMvc.perform(post("/categories")
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnCategoryMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        String expectedName = category.getName();

        ResultActions resultActions = mockMvc.perform(put("/categories/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingUpdateId));
        resultActions.andExpect(jsonPath("$.name").value("BANHO"));
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenInvalidTokenAndAllDataIsValid() throws Exception {
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions resultActions = mockMvc.perform(put("/categories/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnBadRequestWhenDataIsValidAndNameDoesNotUnique() throws Exception {
        category.setName(existsCategoryName);
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions resultActions = mockMvc.perform(put("/categories/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
        category.setName("Co");
        categoryDTO = FactoryCategory.createCategoryDTO(category);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions resultActions = mockMvc.perform(put("/categories/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteShouldNoContentWhenAdminLoggedAndExistsId() throws Exception {

        ResultActions result = mockMvc.perform(delete("/categories/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions result = mockMvc.perform(delete("/categories/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldNotFoundWhenAdminLoggedAndDoesNotExisitsId() throws Exception {

        ResultActions result = mockMvc.perform(delete("/categories/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
