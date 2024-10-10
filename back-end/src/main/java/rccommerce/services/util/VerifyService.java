package rccommerce.services.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rccommerce.dto.UserMinDTO;
import rccommerce.services.UserService;
import rccommerce.services.exceptions.ForbiddenException;

@Service
public class Authentication {
	
	@Autowired
	private UserService service;
	
	private UserMinDTO userLogged;
	
	public void authUser(String auth, Long id) {
		userLogged = service.getMe();

		if(userLogged.getId() == id) {
			return;
		}
		if(userLogged.getAuths().contains("ALL")) {
			return;
		} 
		if(userLogged.getAuths().containsAll(List.of(auth))) {
			return;
		} 
		throw new ForbiddenException("Acesso negado");	
	}
}
