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
import rccommerce.dto.ProductDTO;
import rccommerce.dto.ProductMinDTO;
import rccommerce.services.ProductService;

@Validated
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping(value = "/all")
    public ResponseEntity<Page<ProductMinDTO>> findAll(
            Pageable pageable) {
        Page<ProductMinDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<ProductMinDTO>> findAll(
            @ValidId
            @RequestParam(name = "id", defaultValue = "") String id,
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "reference", defaultValue = "") String reference,
            Pageable pageable) {
        Page<ProductMinDTO> pageMinDto = service.searchEntity(id.isEmpty() ? null : Long.valueOf(id), name, reference, pageable);
        return ResponseEntity.ok(pageMinDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductMinDTO> findById(@ValidId @PathVariable String id) {
        ProductMinDTO dto = service.findById(Long.valueOf(id), false);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/cod/{codBarra}")
    public ResponseEntity<ProductDTO> findByReference(@PathVariable String codBarra) {
        ProductDTO dto = service.findByReference(codBarra);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @PostMapping
    public ResponseEntity<ProductMinDTO> insert(@Valid @RequestBody ProductDTO dto) {
        ProductMinDTO minDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductMinDTO> update(@Valid @RequestBody ProductDTO dto, @PathVariable Long id) {
        ProductMinDTO minDTO = service.update(dto, id);
        return ResponseEntity.ok(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
