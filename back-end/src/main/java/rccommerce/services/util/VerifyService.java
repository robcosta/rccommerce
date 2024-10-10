package rccommerce.services.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rccommerce.dto.UserMinDTO;
import rccommerce.entities.enums.Very;
import rccommerce.services.UserService;
import rccommerce.services.exceptions.ForbiddenException;

@Service
public class VerifyService {
	
	@Autowired
	private UserService service;
	
	private UserMinDTO userLogged;
	
	public void veryUser(Very very, Long id) {
		userLogged = service.getMe();

		if(userLogged.getId() == id) {
			return;
		}
		if(userLogged.getVery().contains(Very.ALL)) {
			return;
		} 
		if(userLogged.getVery().containsAll(List.of(very))) {
			return;
		} 
		throw new ForbiddenException("Acesso negado");	
	}
}
