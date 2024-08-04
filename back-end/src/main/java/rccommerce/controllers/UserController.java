package rccommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_USER')")
	@GetMapping
	public ResponseEntity<Page<UserMinDTO>> findAll(
			@RequestParam(name = "name", defaultValue = "") String name, 
			Pageable pageable) {
		Page<UserMinDTO> dto = service.findAll(name, pageable);
		return ResponseEntity.ok(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_USER')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserMinDTO> findById(@PathVariable Long id) {
		UserMinDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}
}
