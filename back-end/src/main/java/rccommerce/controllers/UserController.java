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

import rccommerce.dto.mindto.UserMinDTO;
import rccommerce.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER','ROLE_CLIENT')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserMinDTO> getMe() {
        UserMinDTO dto = service.getMe();
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @GetMapping
    public ResponseEntity<Page<UserMinDTO>> searchEntity(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String email,
            Pageable pageable) {
        Page<UserMinDTO> dto = service.searchEntity(name, email, pageable);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserMinDTO> findById(@PathVariable Long id) {
        UserMinDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }
}
