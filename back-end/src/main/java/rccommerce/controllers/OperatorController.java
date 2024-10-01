package rccommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.services.OperatorService;
import rccommerce.services.exceptions.ForbiddenException;

@RestController
@RequestMapping(value =  "/operators")
public class OperatorController {
	
	@Autowired
	private OperatorService service;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@GetMapping(value = "/all")
	public ResponseEntity<Page<OperatorMinDTO>> findAll(
//			@RequestParam(name = "name", defaultValue = "") String name,
//			@RequestParam(name = "email", defaultValue = "") String email,
			Pageable pageable) {
		Page<OperatorMinDTO> dto = service.findAll(pageable);
		return ResponseEntity.ok(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@GetMapping(value = "/search")
	public ResponseEntity<Page<OperatorMinDTO>> seachOperator(
			@Valid @RequestBody OperatorMinDTO dto, Pageable pageable) {
		Page<OperatorMinDTO> pageDto = service.searchOperator(dto, pageable);
		return ResponseEntity.ok(pageDto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<OperatorMinDTO> findById(@PathVariable Long id) {
		OperatorMinDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<OperatorMinDTO> insert(@Valid @RequestBody OperatorDTO dto) {
		OperatorMinDTO minDTO = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(minDTO);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<OperatorMinDTO> update(@Valid @RequestBody OperatorDTO dto, @PathVariable Long id) {
		OperatorMinDTO minDTO = service.update(dto, id);
		return ResponseEntity.ok(minDTO);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (id == 1L) {
			throw new ForbiddenException("ADMINSTRADOR MASTER - Deleção proibida");
		}
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
