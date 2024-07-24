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

import rccommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TokenUtil tokenUtil;
	private String adminToken, operatorToken, invalidToken, emptyToken; 
	private String adminUserName, adminPassword, operatorUserName, operatorPasswordString;

	private String userName;

	@BeforeEach
	void setUp() throws Exception {
		userName = "Blue";
		adminUserName = "maria@gmail.com";
		adminPassword = "123456";
		operatorUserName = "bob@gmail.com";
		operatorPasswordString = "123456";
				
		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUserName, adminPassword);
		operatorToken = tokenUtil.obtainAccessToken(mockMvc, operatorUserName, operatorPasswordString);
		invalidToken = adminToken + "xpto";
		
	}
	
	@Test
	public void findAllShouldREturnPageWhenValidTokenAndNameParamIsEmpty() throws Exception {

		ResultActions resultActions = mockMvc
				.perform(get("/users")
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
	public void findAllShouldREturnPageWhenValidTokenAndNameParamIsNotEmpty() throws Exception {

		ResultActions resultActions = mockMvc
				.perform(get("/users?name={userName}", userName)
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
		
		ResultActions resultActions = mockMvc
				.perform(get("/users?name={userName}", userName)
						.header("Authorization", "Bearer " + invalidToken)
						.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isUnauthorized());
	}

}
