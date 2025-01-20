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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import rccommerce.dto.ProductDTO;
import rccommerce.entities.Product;
import rccommerce.tests.FactoryProduct;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    // @Autowired
    // private ProductRepository productRepository;
    private String adminToken, invalidToken;
    private String userAdminEmail, userAdminPassword;
    private String existsProductName, existsProductreference, nonExistsProductName;
    private Long existingId, existingUpdateId, nonExistingId;
    private Integer countProduct;
    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        userAdminEmail = "admin@gmail.com";
        userAdminPassword = "123456";

        existsProductName = "The Lord of the Rings";
        existsProductreference = "0000000000017";

        nonExistsProductName = "MESA";

        existingId = 1L;
        existingUpdateId = 3L;
        nonExistingId = 100L;
        countProduct = 25;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
        invalidToken = adminToken + "xpto";

        product = FactoryProduct.createProduct();
    }

    @Test
    public void findAllShouldREturnPageWhenValidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search?sort=id&size=50")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(countProduct));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsProductName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenParamEmpty() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search?sort=id&size=50&id={id}&name={name}&&reference={reference}", "", "", "")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(countProduct));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsProductName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndIdParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search?id={id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsProductName));
    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndNameParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search?name={name}", existsProductName)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].name").value(existsProductName));

    }

    @Test
    public void searchEntityShouldReturnPageWhenValidTokenAndReferenceParamExisting() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search?reference={reference}", existsProductreference)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content.size()").value(1));
        resultActions.andExpect(jsonPath("$.content[0].id").value(existingId));
        resultActions.andExpect(jsonPath("$.content[0].reference").value(existsProductreference));

    }

    @Test
    public void searchEntityShouldReturnNotFoundWhenIdParamDoesNotExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search?id={id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void searchEntityShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/search")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShoulReturCategoryMinDTOWhenValidTokenAndExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value(existsProductName));
    }

    @Test
    public void findByIdShoulReturNotFoundWhenValidTokenAndDoesNotExistsId() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShoulReturUnaauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnCategoryMinDTOWhenAllDataIsValid() throws Exception {
        productDTO = FactoryProduct.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = product.getName();

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    public void insertShouldReturnBadRequestWhenDataIsValidAndNameDoesNotUnique() throws Exception {
        product.setName(existsProductName);
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.error").value("Nome informado já existe"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidName() throws Exception {
        product.setName("Ro");
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
        resultActions.andExpect(jsonPath("$.error").value("Dados inválidos"));
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        product.setName(existsProductName);
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnCategoryMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = product.getName();

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingUpdateId)
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
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnBadRequestWhenDataIsValidAndNameDoesNotUnique() throws Exception {
        product.setId(existingUpdateId);
        product.setName(existsProductName);
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
        product.setName("Co");
        productDTO = FactoryProduct.createProductDTO(product);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingUpdateId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteShouldNoContentWhenAdminLoggedAndExistsId() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldNotFoundWhenAdminLoggedAndDoesNotExisitsId() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
