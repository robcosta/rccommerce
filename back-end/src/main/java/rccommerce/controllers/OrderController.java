package rccommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import rccommerce.controllers.validators.ValidId;
import rccommerce.dto.OrderDTO;
import rccommerce.dto.OrderMinDTO;
import rccommerce.services.OrderService;

@Validated
@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderMinDTO> findById(@ValidId @PathVariable String id) {
        OrderMinDTO dto = service.findById(Long.valueOf(id));
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    @GetMapping(value = "/my")
    public ResponseEntity<Page<OrderMinDTO>> findMyOrders(Pageable pageable) {

        Page<OrderMinDTO> pageDto = service.searchEntity(pageable);
        return ResponseEntity.ok(pageDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping(value = "/search")
    public ResponseEntity<Page<OrderMinDTO>> searchEntity(
            @ValidId @RequestParam(name = "id", defaultValue = "") String id,
            @ValidId @RequestParam(name = "userid", defaultValue = "") String userid,
            @RequestParam(name = "username", defaultValue = "") String username,
            @ValidId @RequestParam(name = "clientid", defaultValue = "") String clientid,
            @RequestParam(name = "clientname", defaultValue = "") String clientname,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestParam(name = "timeStart", defaultValue = "") String timeStart,
            @RequestParam(name = "timeEnd", defaultValue = "") String timeEnd,
            Pageable pageable) {

        Page<OrderMinDTO> pageDto = service.searchEntity(id, userid, username, clientid, clientname, status, timeStart, timeEnd, pageable);
        return ResponseEntity.ok(pageDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    public ResponseEntity<OrderMinDTO> insert(@Valid @RequestBody OrderDTO dto) {
        OrderMinDTO minDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(minDTO);
    }
}
