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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import rccommerce.dto.SuplierDTO;
import rccommerce.dto.SuplierMinDTO;
import rccommerce.services.SuplierService;

@RestController
@RequestMapping(value =  "/supliers")
public class SuplierController {
	
	@Autowired
	private SuplierService service;
	
	@GetMapping
	public ResponseEntity<Page<SuplierMinDTO>> findAll(
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "cnpj", defaultValue = "") String cnpj,
			Pageable pageable) {
		Page<SuplierMinDTO> dto = service.findAll(name, cnpj, pageable);
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<SuplierDTO> findById(@PathVariable Long id) {
		SuplierDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@PostMapping
	public ResponseEntity<SuplierDTO> insert(@Valid @RequestBody SuplierDTO dto) {
		SuplierDTO minDTO = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(minDTO);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<SuplierDTO> update(@Valid @RequestBody SuplierDTO dto, @PathVariable Long id) {
		SuplierDTO minDTO = service.update(dto, id);
		return ResponseEntity.ok(minDTO);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
