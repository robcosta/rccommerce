package rccommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import rccommerce.dto.PaymentDTO;
import rccommerce.services.PaymentService;

@RestController
@RequestMapping(value =  "/payments")
public class PaymentController {
	
	@Autowired
	private PaymentService service;
	
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<PaymentDTO> findById(@PathVariable Long id) {
		PaymentDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}
	
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@PostMapping
	public ResponseEntity<PaymentDTO> insert(@Valid @RequestBody PaymentDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
}
