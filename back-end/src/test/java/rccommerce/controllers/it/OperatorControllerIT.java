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

import rccommerce.dto.OperatorDTO;
import rccommerce.entities.Operator;
import rccommerce.tests.Factory;
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
	
	
	private String adminToken, operatorToken, invalidToken, emptyToken;
	private String userAdminEmail, userAdminPassword, userOperatorEmail, userOperatorPassword;
	private String existsOperatorName, existsOperatorEmail, nonExistsOperatorName, nonExistsOperatorEmail, emailUnique;

	private long existingId, nonExistingId;
	private Long countTotalUsers;
	
	private Operator operator;
	private OperatorDTO operatorDTO;

	@BeforeEach
	void setUp() throws Exception {
		userAdminEmail = "maria@gmail.com";
		userAdminPassword = "123456";
		userOperatorEmail = "bob@gmail.com";
		userOperatorPassword = "123456";
		
		existsOperatorName = "Alex Blue";
		existsOperatorEmail = "alex@gmail.com";
		
		nonExistsOperatorName = "Peter Black";
		nonExistsOperatorEmail = "peter@gmail.com";
		
		emailUnique = "bob@gmail.com";

		existingId = 3L;
		nonExistingId = 100L;

		adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
		operatorToken = tokenUtil.obtainAccessToken(mockMvc, userOperatorEmail, userOperatorPassword);
		invalidToken = adminToken + "xpto";
		countTotalUsers = 5L;
		
		operator = Factory.createOperator();
		operator.setId(null);
	}

	@Test
	public void findAllShouldREturnPageWhenValidTokenAndNameAndEmailParamsAreEmptys() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/operators")
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content.size()").value(3));
		resultActions.andExpect(jsonPath("$.content[0].id").value(1L));
		resultActions.andExpect(jsonPath("$.content[0].name").value("Maria Brown"));
		resultActions.andExpect(jsonPath("$.content[0].email").value("maria@gmail.com"));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_ADMIN"));
		resultActions.andExpect(jsonPath("$.content[0].roles[1]").value("ROLE_OPERATOR"));
		resultActions.andExpect(jsonPath("$.content[0].roles[2]").value("ROLE_SELLER"));

	}

	@Test
	public void findAllShouldReturnPageWhenValidTokenAndNameParamIsNotEmpty() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/operators?name={existsOperatorName}", existsOperatorName)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content[0].id").value(3L));
		resultActions.andExpect(jsonPath("$.content[0].name").value("Alex Blue"));
		resultActions.andExpect(jsonPath("$.content[0].email").value("alex@gmail.com"));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_SELLER"));

	}
	
	@Test
	public void findAllShouldReturnPageWhenValidTokenAndEmailParamIsNotEmpty() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/operators?email={existsOperatorEmail}", existsOperatorEmail)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content[0].id").value(3L));
		resultActions.andExpect(jsonPath("$.content[0].name").value("Alex Blue"));
		resultActions.andExpect(jsonPath("$.content[0].email").value("alex@gmail.com"));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_SELLER"));
		
	}
	
	@Test
	public void findAllShouldReturnPageWhenValidTokenAndNameNotExisting() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/operators?name={nonExistsName}", nonExistsOperatorName)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void findAllShouldReturnPageWhenValidTokenAndEmailNotExisting() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/operators?email={nonExistsEmail}", nonExistsOperatorEmail)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isNotFound());
		
	}

	@Test
	public void findAllShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/operators?name={existsOperatorName}", existsOperatorName)
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
		resultActions.andExpect(jsonPath("$.name").value("Alex Blue"));
		resultActions.andExpect(jsonPath("$.email").value("alex@gmail.com"));
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_SELLER"));
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
		operatorDTO = Factory.createOperatorDTO(operator);
		String jsonBody = objectMapper.writeValueAsString(operatorDTO);
		
		String expectedName =  operator.getName();
		String expectedEmail = operator.getEmail();
		
		ResultActions resultActions = mockMvc.perform(post("/operators")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.id").value(countTotalUsers + 1L));
		resultActions.andExpect(jsonPath("$.name").value(expectedName));
		resultActions.andExpect(jsonPath("$.email").value(expectedEmail));
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenOperatorLoggedAndAllDataIsValid() throws Exception {
		operatorDTO = Factory.createOperatorDTO(operator);
		String jsonBody = objectMapper.writeValueAsString(operatorDTO);

		ResultActions resultActions = mockMvc.perform(post("/operators")
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isForbidden());
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenOperatorLoggedAndAllDataIsValid() throws Exception {
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operator.addRole(Factory.createRole());
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operator.setCommission(-1.0);
		
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operator.setPassword("12A34B");;
		operator.addRole(Factory.createRole());
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
		String jsonBody = objectMapper.writeValueAsString(operatorDTO);
		
		String expectedName =  operator.getName();
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
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
	}
	
	@Test
	public void updateShouldReturnForbiddenWhenOperatorLoggedAndAllDataIsValid() throws Exception {
		operatorDTO = Factory.createOperatorDTO(operator);
		String jsonBody = objectMapper.writeValueAsString(operatorDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/operators/{id}", existingId)
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isForbidden());
	}
	
	@Test
	public void updateShouldReturnUnauthorizedWhenInvalidTokenAndAllDataIsValid() throws Exception {
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operator.setCommission(-0.15);
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
		operatorDTO = Factory.createOperatorDTO(operator);
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
				
		ResultActions result = 
				mockMvc.perform(delete("/operators/{id}", existingId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());		
	}
	
	@Test 
	void deleteShouldForbiddenWhenAdminLoggedAndDeleteYourself() throws Exception {
		existingId = 1L;
		
		ResultActions result = 
				mockMvc.perform(delete("/operators/{id}", existingId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isForbidden());
		
	}
	
	@Test 
	void deleteShouldForbiddenWhenOperatorLogged() throws Exception {
				
		ResultActions result = 
				mockMvc.perform(delete("/operators/{id}", existingId)
						.header("Authorization", "Bearer " + operatorToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isForbidden());		
	}
	
	@Test 
	void deleteShouldUnauthorizedWhenInvalidToken() throws Exception {
				
		ResultActions result = 
				mockMvc.perform(delete("/operators/{id}", existingId)
						.header("Authorization", "Bearer " + invalidToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isUnauthorized());		
	}
	
	@Test 
	void deleteShouldNotFoundWhenAdminLoggedAndDoesNotExisitsId() throws Exception {
				
		ResultActions result = 
				mockMvc.perform(delete("/operators/{id}", nonExistingId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());		
	}
}
