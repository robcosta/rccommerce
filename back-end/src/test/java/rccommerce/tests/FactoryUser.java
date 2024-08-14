package rccommerce.tests;

import java.util.ArrayList;
import java.util.List;

import rccommerce.dto.ClientDTO;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.Operator;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.entities.enums.Auth;
import rccommerce.projections.UserDetailsProjection;

public class FactoryUser {
	
	public static User createUser() {
		User user = new User(6L, "Robert Black", "robert@gmail.com", "123456");
		return user;
	}
	
	public static Role createRoleAdmin() {
		Role role = new Role(1L, "ROLE_ADMIN");
		return role;
	}
	
	public static Role createRoleOperator() {
		Role role = new Role(3L, "ROLE_OPERATOR");
		return role;
	}
	
	public static Role createRoleClient() {
		Role role = new Role(2L, "ROLE_CLIENT");
		return role;
	}
	
	public static UserDTO createUserDTO() {
		return new UserDTO(createUser());
	}
	
	public static UserDTO createUserDTO(User user) {
		return new UserDTO(user);
	}
	
	public static UserMinDTO createUserMinDTO() {
		return new UserMinDTO(createUser());
	}
	
	public static UserMinDTO createUserMinDTO(User user) {
		return new UserMinDTO(user);
	}
	
	public static List<UserDetailsProjection> createUserDetails(){
		List<UserDetailsProjection> list = new ArrayList<>();		
		list.add(new UserDetailsImpl("robert@gmail.com","$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze", 1L, "ROLE_CLIENT"));
		return list;	
	}

	public static Operator createOperator() {
		Operator operator = new Operator(7L, "Ana Pink", "ana@gmail.com", "123456", 1.5);
		return operator;
	}
	
	public static Operator createOperator(User user) {
		Operator operator = new Operator(user.getId(), user.getName(), user.getEmail(), user.getPassword(), 1.5);	
		return operator;
	}
	
	public static OperatorDTO createOperatorDTO(Operator operator) {
		OperatorDTO operatorDto = new OperatorDTO(operator);
		return operatorDto;
	}
	
	public static Client createClient() {
		Client client = new Client(7L, "Ana Pink", "ana@gmail.com", "123456", "59395734019");
		client.addRole(createRoleClient());
		client.addAuth(Auth.NONE);;
		return client;
	}
	
	public static ClientDTO createClientDTO(Client client) {
		ClientDTO clientDto = new ClientDTO(client);
		return clientDto;
	}

}


//Classe auxilar para implementação de uma instância de UserDetailsProjection
class UserDetailsImpl implements UserDetailsProjection {
	
	private String username;
	private String password;
	private Long roleId;
	private String authority;
	
	public UserDetailsImpl(String username, String password, Long roleId, String authority) {
		this.username = username;
		this.password = password;
		this.roleId = roleId;
		this.authority = authority;
	}
	
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Long getRoleId() {
		return roleId;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
	
}



