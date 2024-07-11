package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rccommerce.entity.User;
import rccommerce.exceptions.ForbiddenException;

@Service
public class AuthService {
	
	@Autowired
	private UserService userService;
	
	public void validateSelfOrAdmin(Long userId) {
		User me = userService.authenticated();

		if(me.hasRole("ROLE_ADMIN")) {
			return;
		}
		
		if(!me.getId().equals(userId)) {
			throw new ForbiddenException("Access denied. Should be self or admins");
		}
	}
}
