package rccommerce.tests;

import rccommerce.dto.UserDTO;
import rccommerce.entities.Role;
import rccommerce.entities.User;

public class Factory {
	
	public static User createUserAdmin() {
		User user = new User(4L, "Robert Black", "robert@gmail.com", 2.0, "123456");
		user.addRole(new Role(1L, "ROLE_ADMIN"));
		return user;
	}
	
	public static User createUserOperator() {
		User user = new User(5L, "Peter Wellow", "peter@gmail.com", 2.0, "123456");
		user.addRole(new Role(2L, "ROLE_OPERATOR"));
		return user;
	}
	
	public static UserDTO createUserDTOAdmin() {
		return new UserDTO(createUserAdmin());
	}
	
	public static UserDTO createUserDTOOperator() {
		return new UserDTO(createUserOperator());
	}

}
