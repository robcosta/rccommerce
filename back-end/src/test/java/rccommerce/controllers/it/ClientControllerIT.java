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

import rccommerce.dto.ClientDTO;
import rccommerce.entities.Client;
import rccommerce.tests.FactoryUser;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClientControllerIT {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenUtil tokenUtil;
	
	
	private String adminToken, clientToken, invalidToken;
	private String userAdminEmail, userAdminPassword, userClientEmail, userClientPassword;
	private String existsClientName, existsClientEmail, existsClientCpf,existsEmail;
	private String nonExistsClientName, nonExistsClientEmail, nonExistsClientCpf; 

	private long existingId, nonExistingId;
	
	private Client client;
	private ClientDTO clientDTO;

	@BeforeEach
	void setUp() throws Exception {
		userAdminEmail = "admin@gmail.com";
		userAdminPassword = "123456";
		userClientEmail = "peter@gmail.com";
		userClientPassword = "123456";
		
		existsClientName = "Peter Yellow";
		existsClientEmail = "peter@gmail.com";
		existsClientCpf = "46311990083";
		
		nonExistsClientName = "Peter Black";
		nonExistsClientEmail = "other@gmail.com";
		nonExistsClientCpf = "37730902001";
		
		existsEmail = "bob@gmail.com";

		existingId = 4L;
		nonExistingId = 100L;

		adminToken = tokenUtil.obtainAccessToken(mockMvc, userAdminEmail, userAdminPassword);
		clientToken = tokenUtil.obtainAccessToken(mockMvc, userClientEmail, userClientPassword);
		invalidToken = adminToken + "xpto";
		
		client = FactoryUser.createClient();
		//client.setId(null);
	}

	@Test
	public void findAllShouldREturnPageWhenValidTokenAndEmptyParams() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/clients?sort=id")
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content.size()").value(2));
		resultActions.andExpect(jsonPath("$.content[0].id").value(4L));
		resultActions.andExpect(jsonPath("$.content[0].name").value("John Red"));
		resultActions.andExpect(jsonPath("$.content[0].email").value("john@gmail.com"));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_CLIENT"));
	}

	@Test
	public void findAllShouldReturnPageWhenValidTokenAndNameParamExisting() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/clients?name={name}", existsClientName)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content[0].id").value(5L));
		resultActions.andExpect(jsonPath("$.content[0].name").value(existsClientName));
		resultActions.andExpect(jsonPath("$.content[0].email").value(existsClientEmail));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_CLIENT"));

	}
	
	@Test
	public void findAllShouldReturnPageWhenValidTokenAndEmailParamExisting() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients?email={email}", existsClientEmail)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content[0].id").value(5L));
		resultActions.andExpect(jsonPath("$.content[0].name").value(existsClientName));
		resultActions.andExpect(jsonPath("$.content[0].email").value(existsClientEmail));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_CLIENT"));
		
	}
	
	@Test
	public void findAllShouldReturnPageWhenValidTokenAndCpfParamExisitng() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients?cpf={cpf}", existsClientCpf)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content[0].id").value(5L));
		resultActions.andExpect(jsonPath("$.content[0].name").value(existsClientName));
		resultActions.andExpect(jsonPath("$.content[0].email").value(existsClientEmail));
		resultActions.andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_CLIENT"));
		
	}
	
	@Test
	public void findAllShouldReturnNotFoundWhenNameParamDoesNotExists() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/clients?name={name}", nonExistsClientName)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnNotFoundWhenEmailParamDoesNotExists() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients?email={email}", nonExistsClientEmail)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnNotFoundWhenCpfParamDoesNotExists() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients?cpf={cpf}", nonExistsClientCpf)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnUnauthorizedWhenIvalidToken() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients")
				.header("Authorization", "Bearer " + invalidToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void findByIdShoulReturClientMinDTOWhenValidTokenAndExistsId() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").value(existingId));
		resultActions.andExpect(jsonPath("$.name").value("John Red"));
		resultActions.andExpect(jsonPath("$.cpf").value("739.958.080-42"));
		resultActions.andExpect(jsonPath("$.email").value("john@gmail.com"));
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENT"));
	}

	@Test
	public void findByIdShoulReturNotFoundWhenValidTokenAndDoesNotExistsId() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients/{id}", nonExistingId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void findByIdShoulReturUnaauthorizedWhenInvalidToken() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + invalidToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void insertShouldReturnClientMinDTOWhenAllDataIsValid() throws Exception {
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
	
		String expectedName =  client.getName();
		String expectedEmail = client.getEmail();
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.name").value(expectedName));
		resultActions.andExpect(jsonPath("$.email").value(expectedEmail));
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENT"));
	}
	

	@Test
	public void insertShouldReturnBadRequestWhenDataIsValidAndEmailDoesNotUnique() throws Exception {
		client.setEmail(existsEmail);
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isBadRequest());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenInvalidName() throws Exception {
		client.setName("Ro");
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenInvalidEmail() throws Exception {
		client.setEmail("roberto.com");
		client.addRole(FactoryUser.createRoleClient());
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenInvalidCpf() throws Exception {
		client.setCpf("12345678911");
		client.addRole(FactoryUser.createRoleClient());
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenInvalidPassword() throws Exception {
		client.setPassword("12A34B");;
		client.addRole(FactoryUser.createRoleClient());
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnBadRequestWhenInvalidPasswordXXXXXXXXX() throws Exception {
		client.setEmail(existsEmail);;
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/clients")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isBadRequest());
	}
	
	@Test
	public void updateShouldReturnClientMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		String expectedName =  client.getName();
		String expectedEmail = client.getEmail();
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").value(existingId));
		resultActions.andExpect(jsonPath("$.name").value(expectedName));
		resultActions.andExpect(jsonPath("$.email").value(expectedEmail));
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENT"));
	}
	
	@Test
	public void updateShouldReturnForbiddenWhenClientLoggedAndAllDataIsValid() throws Exception {
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + clientToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isForbidden());
	}
	
	@Test
	public void updateShouldReturnUnauthorizedWhenInvalidTokenAndAllDataIsValid() throws Exception {
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + invalidToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void updateShouldReturnBadRequestWhenDataIsValidAndEmailDoesNotUnique() throws Exception {
		client.setEmail(existsEmail);
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isBadRequest());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
		client.setName("");
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidEmail() throws Exception {
		client.setEmail("roberto@");
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidComission() throws Exception {
		client.setCpf("12345678911");
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updatetShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidPasswor() throws Exception {
		client.setPassword("95-1");
		clientDTO = FactoryUser.createClientDTO(client);
		String jsonBody = objectMapper.writeValueAsString(clientDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/clients/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	

	@Test 
	void deleteShouldNoContentWhenAdminLoggedAndDoNotDeleteYourself() throws Exception {
				
		ResultActions result = 
				mockMvc.perform(delete("/clients/{id}", existingId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());		
	}
	
	@Test 
	void deleteShouldUnauthorizedWhenInvalidToken() throws Exception {
				
		ResultActions result = 
				mockMvc.perform(delete("/clients/{id}", existingId)
						.header("Authorization", "Bearer " + invalidToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isUnauthorized());		
	}
	
	@Test 
	void deleteShouldNotFoundWhenAdminLoggedAndDoesNotExisitsId() throws Exception {
				
		ResultActions result = 
				mockMvc.perform(delete("/clients/{id}", nonExistingId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());		
	}
}
