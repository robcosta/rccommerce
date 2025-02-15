package rccommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import rccommerce.controllers.validators.ValidId;
import rccommerce.dto.ClientDTO;
import rccommerce.dto.fulldto.ClientFullDTO;
import rccommerce.dto.mindto.ClientMinDTO;
import rccommerce.services.ClientService;

@Validated
@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private ClientService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @GetMapping(value = "/search")
    public ResponseEntity<Page<ClientMinDTO>> searchEntity(
            @ValidId @RequestParam(defaultValue = "") String id,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String cpf, Pageable pageable) {

        Page<ClientMinDTO> pageDto = service.searchEntity(id.isEmpty() ? null : Long.valueOf(id), name, email, cpf,
                pageable);
        return ResponseEntity.ok(pageDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientFullDTO> findById(@ValidId @PathVariable String id) {
        ClientFullDTO dto = service.findByIdWithAddresses(Long.valueOf(id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ClientMinDTO> insert(@Valid @RequestBody ClientDTO dto) {
        ClientMinDTO minDTO = service.insert(dto, false);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ClientMinDTO> update(@Valid @RequestBody ClientDTO dto,
            @ValidId(checkSpecialValues = true) // Verifica se a String Id é Long, e também, se é '1L' ou '4L'
            @PathVariable String id) {
        ClientMinDTO minDTO = service.update(dto, Long.valueOf(id));
        return ResponseEntity.ok(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@ValidId(checkSpecialValues = true) // Verifica se a String Id é Long, e também,
            // se é '1L' ou '4L'
            @PathVariable String id) {
        service.delete(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}
