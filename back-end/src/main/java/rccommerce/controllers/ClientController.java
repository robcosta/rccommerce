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
import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.services.ClientService;
import rccommerce.services.exceptions.ForbiddenException;

@RestController
@RequestMapping(value =  "/clients")
public class ClientController {
	
	@Autowired
	private ClientService service;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@GetMapping(value = "/all")
	public ResponseEntity<Page<ClientMinDTO>> findAll(
//			@RequestParam(name = "name", defaultValue = "") String name,
//			@RequestParam(name = "email", defaultValue = "") String email,
//			@RequestParam(name = "cpf", defaultValue = "") String cpf,
			Pageable pageable) {
		Page<ClientMinDTO> dto = service.findAll(pageable);
		return ResponseEntity.ok(dto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@GetMapping(value = "/search")
	public ResponseEntity<Page<ClientMinDTO>> seachClient(
			@Valid @RequestBody ClientMinDTO dto, Pageable pageable) {
		Page<ClientMinDTO> pageDto = service.searchClient(dto, pageable);
		return ResponseEntity.ok(pageDto);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<ClientMinDTO> findById3(@PathVariable Long id) {
		ClientMinDTO dto = service.findById(id);
		return ResponseEntity.ok(dto);
	}
	
	@PostMapping
	public ResponseEntity<ClientMinDTO> insert(@Valid @RequestBody ClientDTO dto) {
		ClientMinDTO minDTO = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(minDTO);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<ClientMinDTO> update(@Valid @RequestBody ClientDTO dto, @PathVariable Long id) {
		if(id == 4L) {
			throw new ForbiddenException("Não é permitido atualizar o cliente: 'Venda ao Consumidor'");
		}
		ClientMinDTO minDTO = service.update(dto, id);
		return ResponseEntity.ok(minDTO);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if(id == 4L) {
			throw new ForbiddenException("Não é permitido deletar o cliente: 'Venda ao Consumidor'");
		}
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
