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
import rccommerce.dto.SuplierDTO;
import rccommerce.dto.SuplierMinDTO;
import rccommerce.services.SuplierService;

@Validated
@RestController
@RequestMapping(value = "/supliers")
public class SuplierController {

    @Autowired
    private SuplierService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @GetMapping(value = "/search")
    public ResponseEntity<Page<SuplierMinDTO>> searchEntity(
            @ValidId @RequestParam(defaultValue = "") String id,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String cnpj, Pageable pageable) {

        Page<SuplierMinDTO> pageDto = service.searchEntity(id.isEmpty() ? null : Long.valueOf(id), name, cnpj,
                pageable);
        return ResponseEntity.ok(pageDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<SuplierMinDTO> findById(@ValidId @PathVariable String id) {
        SuplierMinDTO dto = service.findById(Long.valueOf(id));
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @PostMapping
    public ResponseEntity<SuplierMinDTO> insert(@Valid @RequestBody SuplierDTO dto) {
        SuplierMinDTO minDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<SuplierMinDTO> update(@Valid @RequestBody SuplierDTO dto, @PathVariable Long id) {
        SuplierMinDTO minDTO = service.update(dto, id);
        return ResponseEntity.ok(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
