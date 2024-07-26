package rccommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Console;

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

import rccommerce.dto.UserDTO;
import rccommerce.entities.User;
import rccommerce.tests.Factory;
import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenUtil tokenUtil;
	
	
	private String adminToken, operatorToken, invalidToken, emptyToken;
	private String adminUserName, adminPassword, operatorUserName, operatorPasswordString;
	private String userName, emailUnique;

	private long existingId, nonExistingId;
	private Long countTotalUsers;
	
	private User user;
	private UserDTO userDTO;

	@BeforeEach
	void setUp() throws Exception {
		userName = "Blue";
		adminUserName = "maria@gmail.com";
		adminPassword = "123456";
		operatorUserName = "bob@gmail.com";
		operatorPasswordString = "123456";
		emailUnique = "bob@gmail.com";

		existingId = 1L;
		nonExistingId = 100L;

		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUserName, adminPassword);
		operatorToken = tokenUtil.obtainAccessToken(mockMvc, operatorUserName, operatorPasswordString);
		invalidToken = adminToken + "xpto";
		countTotalUsers = 3L;
		
		user = Factory.createUser();
		user.addRole(Factory.createRole());
		user.setId(null);
	}
	
	@Test
	public void getMeShouldReturnUserLoggedWhenValidToken() throws Exception {
		
		ResultActions resultActions = mockMvc.perform(get("/users/me")
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").value(1L));
		resultActions.andExpect(jsonPath("$.name").value("Maria Brown"));
		resultActions.andExpect(jsonPath("$.email").value("maria@gmail.com"));
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
	public void findAllShouldREturnPageWhenValidTokenAndNameParamIsEmpty() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/users")
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
	public void findAllShouldReturnUnauthorizedWhenIvalidToken() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/users?name={userName}", userName)
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
		resultActions.andExpect(jsonPath("$.id").value(1L));
		resultActions.andExpect(jsonPath("$.name").value("Maria Brown"));
		resultActions.andExpect(jsonPath("$.email").value("maria@gmail.com"));
		resultActions.andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
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
	
	@Test
	public void insertShouldReturnUserMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		String expectedName =  user.getName();
		String expectedEmail = user.getEmail();
		
		ResultActions resultActions = mockMvc.perform(post("/users")
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
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);

		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isForbidden());
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenOperatorLoggedAndAllDataIsValid() throws Exception {
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + invalidToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void insertShouldReturnBadRequestWhenOperatorLoggedAndAllDataIsValidAndEmailDoesNotUnique() throws Exception {
		user.setEmail(emailUnique);
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isBadRequest());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
		user.setName("Ro");
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidEmail() throws Exception {
		user.setEmail("roberto.com");
		user.addRole(Factory.createRole());
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidCommission() throws Exception {
		user.setCommission(-1.0);
		user.addRole(Factory.createRole());
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidPassword() throws Exception {
		user.setPassword("12A34B");;
		user.addRole(Factory.createRole());
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidRoles() throws Exception {
		user.getRoles().clear();
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updateShouldReturnUserMinDTOWhenAdminLoggedAndAllDataIsValid() throws Exception {
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		String expectedName =  user.getName();
		String expectedEmail = user.getEmail();
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
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
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isForbidden());
	}
	
	@Test
	public void updateShouldReturnUnauthorizedWhenInvalidTokenAndAllDataIsValid() throws Exception {
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + invalidToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void updateShouldReturnBadRequestWhenOperatorLoggedAndAllDataIsValidAndEmailDoesNotUnique() throws Exception {
		user.setEmail(emailUnique);
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isBadRequest());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
		user.setName("");
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidEmail() throws Exception {
		user.setEmail("roberto@");
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidComission() throws Exception {
		user.setCommission(-0.15);
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updatetShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidPasswor() throws Exception {
		user.setPassword("95-1");
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidRoles() throws Exception {
		user.getRoles().clear();
		userDTO = Factory.createUserDTO(user);
		String jsonBody = objectMapper.writeValueAsString(userDTO);
		
		ResultActions resultActions = mockMvc.perform(put("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnprocessableEntity());
	}

}
