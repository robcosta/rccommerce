package rccommerce.tests;

import java.util.ArrayList;
import java.util.List;

import rccommerce.dto.OperatorDTO;
import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.Operator;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;

public class Factory {
	
	public static User createUser() {
		User user = new User(6L, "Robert Black", "robert@gmail.com", "123456");
		return user;
	}
	
	public static Role createRole() {
		Role role = new Role(1L, "ROLE_ADMIN");
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
	
	public static List<UserDetailsProjection> createUserDetails(){
		List<UserDetailsProjection> list = new ArrayList<>();		
		list.add(new UserDetailsImpl("robert@gmail.com","$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze", 1L, "ROLE_CLIENT"));
		return list;	
	}

	public static Operator createOperator() {
		Operator operator = new Operator(7L, "Ana Pink", "ana@gmail.com", "123456", 1.5);	
		operator.getRoles().add(createRole());
		return operator;
	}
	
	public static OperatorDTO createOperatorDTO(Operator operator) {
		OperatorDTO operatorDto = new OperatorDTO(operator);
		return operatorDto;
	}
	
	public static Client createClient() {
		Client client = new Client(7L, "Ana Pink", "ana@gmail.com", "123456", "11111111111");
		return client;
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



