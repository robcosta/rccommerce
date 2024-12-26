package rccommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    // @GetMapping(value = "/search")
    // public ResponseEntity<Page<OrderMinDTO>> searchEntity(
    //         @ValidId @RequestParam(name = "id", defaultValue = "") String id,
    //         @RequestParam(name = "status", defaultValue = "") String status,
    //         @RequestParam(name = "payment", defaultValue = "") String payment,
    //         @ValidId @RequestParam(name = "userid", defaultValue = "") String userId,
    //         @RequestParam(name = "user", defaultValue = "") String user,
    //         @ValidId @RequestParam(name = "clientid", defaultValue = "") String clientId,
    //         @RequestParam(name = "client", defaultValue = "") String client, Pageable pageable) {
    //     Page<OrderMinDTO> pageDto = service.searchEntity(
    //             id.isEmpty() ? null : Long.valueOf(id),
    //             status, payment,
    //             userId.isEmpty() ? null : Long.valueOf(userId),
    //             user,
    //             clientId.isEmpty() ? null : Long.valueOf(clientId),
    //             client, pageable);
    //     return ResponseEntity.ok(pageDto);
    // }
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

    @PostMapping("/close")
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
}
