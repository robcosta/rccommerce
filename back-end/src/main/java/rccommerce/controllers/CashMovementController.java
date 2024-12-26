package rccommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rccommerce.services.CashMovementService;

@Validated
@RestController
@RequestMapping(value = "/cash")
public class CashMovementController {

    @Autowired
    private CashMovementService service;

    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    // @GetMapping(value = "/{id}")
    // public ResponseEntity<OrderMinDTO> findById(@ValidId @PathVariable String id) {
    //     OrderMinDTO dto = service.findById(Long.valueOf(id));
    //     return ResponseEntity.ok(dto);
    // }
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
    // // }
    // @PostMapping("/open")
    // @PreAuthorize("hasAnyRole('ROLE_CASH')")
    // public ResponseEntity<CashMovementMinDTO> openingBalance(@Valid @RequestBody CashMovementDTO dto) {
    //     long startTime = System.currentTimeMillis();
    //     CashMovementMinDTO minDTO = service.openingBalance(dto);
    //     URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
    //     long endTime = System.currentTimeMillis();
    //     long queryTime = endTime - startTime;
    //     System.out.println("**************************************************************************************************************");
    //     System.out.println("TEMPO TOTAL: " + queryTime);
    //     System.out.println("**************************************************************************************************************");
    //     return ResponseEntity.created(uri).body(minDTO);
    // }
    // @PostMapping("/close")
    // @PreAuthorize("hasAnyRole('ROLE_CASH')")
    // public ResponseEntity<CashReportMinDTO> closeCashRegister(@Valid @RequestBody CashMovementDTO dto) {
    //     service.closeCashRegister(dto);
    //     return ResponseEntity.ok().build();
    // }
}
