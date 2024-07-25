package rccommerce.tests;

import java.util.ArrayList;
import java.util.List;

import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;

public class Factory {
	
	public static User createUser() {
		User user = new User(4L, "Robert Black", "robert@gmail.com", 2.0, "123456");
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



