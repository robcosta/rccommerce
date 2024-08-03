package rccommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rccommerce.dto.UserMinDTO;
import rccommerce.services.UserService;

@RestController
@RequestMapping(value =  "/users")
public class UserController {
	
	@Autowired
	private UserService service;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_USER')")
	@GetMapping(value = "/me")
	public ResponseEntity<UserMinDTO> getMe() {
		UserMinDTO dto = service.getMe();
		return ResponseEntity.ok(dto);
	}	
}
