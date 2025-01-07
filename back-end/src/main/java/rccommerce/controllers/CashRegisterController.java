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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import rccommerce.controllers.validators.ValidId;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
import rccommerce.dto.CashReportMinDTO;
import rccommerce.services.CashRegisterService;

@Validated
@RestController
@RequestMapping(value = "/cashregister")
public class CashRegisterController {

    @Autowired
    private CashRegisterService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<CashRegisterMinDTO> findById(@ValidId @PathVariable String id) {
        CashRegisterMinDTO dto = service.findById(Long.valueOf(id));
        return ResponseEntity.ok(dto);
    }

    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    // @GetMapping(value = "/my")
    // public ResponseEntity<Page<OrderMinDTO>> findMyOrders(Pageable pageable) {
    //     Page<OrderMinDTO> pageDto = service.searchEntity(pageable);
    //     return ResponseEntity.ok(pageDto);
    // }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CASH')")
    @GetMapping(value = "/search")
    public ResponseEntity<Page<CashRegisterMinDTO>> searchEntity(
            @ValidId @RequestParam(name = "id", defaultValue = "") String id,
            @ValidId @RequestParam(name = "operatorId", defaultValue = "") String operatorId,
            @RequestParam(name = "status", defaultValue = "") String status,
            Pageable pageable) {
        Page<CashRegisterMinDTO> pageDto = service.searchEntity(id, operatorId, status, pageable);
        return ResponseEntity.ok(pageDto);
    }

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('ROLE_CASH')")
    public ResponseEntity<CashRegisterMinDTO> openBalance(@Valid @RequestBody CashRegisterDTO dto) {
        long startTime = System.currentTimeMillis();
        CashRegisterMinDTO minDTO = service.openBalance(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;
        System.out.println("**************************************************************************************************************");
        System.out.println("TEMPO TOTAL: " + queryTime);
        System.out.println("**************************************************************************************************************");
        return ResponseEntity.created(uri).body(minDTO);
    }

    @PutMapping("/close")
    @PreAuthorize("hasAnyRole('ROLE_CASH')")
    public ResponseEntity<CashReportMinDTO> closeBalance(@Valid @RequestBody CashRegisterDTO dto) {
        long startTime = System.currentTimeMillis();
        CashReportMinDTO minDTO = service.closingBalance(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;
        System.out.println("**************************************************************************************************************");
        System.out.println("TEMPO TOTAL: " + queryTime);
        System.out.println("**************************************************************************************************************");
        return ResponseEntity.created(uri).body(minDTO);
    }

    @PutMapping("/register")
    @PreAuthorize("hasAnyRole('ROLE_CASH')")
    public ResponseEntity<CashRegisterMinDTO> registerBalance(@Valid @RequestBody CashRegisterDTO dto) {
        long startTime = System.currentTimeMillis();
        CashRegisterMinDTO minDTO = service.registerBalance(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;
        System.out.println("**************************************************************************************************************");
        System.out.println("TEMPO TOTAL: " + queryTime);
        System.out.println("**************************************************************************************************************");
        return ResponseEntity.created(uri).body(minDTO);
    }
}
