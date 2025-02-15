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
import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.mindto.ProductCategoryMinDTO;
import rccommerce.services.CategoryService;

@Validated
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @GetMapping(value = "/all")
    public ResponseEntity<Page<ProductCategoryMinDTO>> findAll(Pageable pageable) {
        Page<ProductCategoryMinDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @GetMapping(value = "/search")
    public ResponseEntity<Page<ProductCategoryMinDTO>> searchEntity(
            @ValidId
            @RequestParam(defaultValue = "") String id,
            @RequestParam(defaultValue = "") String name,
            Pageable pageable) {

        Page<ProductCategoryMinDTO> dto = service.searchEntity(id.isEmpty() ? null : Long.valueOf(id), name, pageable);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductCategoryMinDTO> findById(@ValidId @PathVariable String id) {
        ProductCategoryMinDTO dto = service.findById(Long.valueOf(id));
        return ResponseEntity.ok(dto);
    }

//	@GetMapping(value = "/name/{name}")
//	public ResponseEntity<CategoryDTO> findByReference(@PathVariable String name) {
//		CategoryDTO dto = service.findByName(name);
//		return ResponseEntity.ok(dto);
//	}
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @PostMapping
    public ResponseEntity<ProductCategoryMinDTO> insert(@Valid @RequestBody ProductCategoryDTO dto) {
        ProductCategoryMinDTO minDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductCategoryMinDTO> update(@Valid @RequestBody ProductCategoryDTO dto, @ValidId @PathVariable String id) {
        ProductCategoryMinDTO minDTO = service.update(dto, Long.valueOf(id));
        return ResponseEntity.ok(minDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@ValidId @PathVariable String id) {
        service.delete(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}
