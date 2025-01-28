package rccommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
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
import rccommerce.dto.CashClosingMinDTO;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
import rccommerce.services.CashRegisterService;
import rccommerce.util.CustomPage;

@Validated
@RestController
@RequestMapping(value = "/cashregister")
public class CashRegisterController {

    @Autowired
    private CashRegisterService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<CashRegisterMinDTO> findById(@ValidId @PathVariable String id) {
        long startTime = System.currentTimeMillis();
        CashRegisterMinDTO dto = service.findById(Long.valueOf(id));
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;
        System.out.println("**************************************************************************************************************");
        System.out.println("TEMPO TOTAL: " + queryTime);
        System.out.println("**************************************************************************************************************");
        return ResponseEntity.ok(dto);
    }

    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SELLER', 'ROLE_CLIENT')")
    // @GetMapping(value = "/my")
    // public ResponseEntity<Page<OrderMinDTO>> findMyOrders(Pageable pageable) {
    //     Page<OrderMinDTO> pageDto = service.searchEntity(pageable);
    //     return ResponseEntity.ok(pageDto);
    // }
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CASH')")
    // @GetMapping(value = "/search")
    // public ResponseEntity<Page<CashRegisterMinDTO>> searchEntity(
    //         @ValidId @RequestParam(name = "id", defaultValue = "") String id,
    //         @ValidId @RequestParam(name = "operatorId", defaultValue = "") String operatorId,
    //         @RequestParam(name = "status", defaultValue = "") String status,
    //         @RequestParam(name = "timeStart", defaultValue = "") String timeStart,
    //         @RequestParam(name = "timeEnd", defaultValue = "") String timeEnd,
    //         Pageable pageable) {
    //     Page<CashRegisterMinDTO> pageDto = service.searchEntity(id, operatorId, status, timeStart, timeEnd, pageable);
    //     return ResponseEntity.ok(pageDto);
    // }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CASH')")
    @GetMapping(value = "/total-sales")
    public ResponseEntity<CustomPage<CashRegisterMinDTO>> searchEntity(
            @ValidId @RequestParam(name = "id", defaultValue = "") String id,
            @RequestParam(name = "operatorid", defaultValue = "") String operatorId,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestParam(name = "timestart", defaultValue = "") String timeStart,
            @RequestParam(name = "timeend", defaultValue = "") String timeEnd,
            @RequestParam(name = "cashmovementtype", defaultValue = "") String cashMovementType,
            @RequestParam(name = "movementtype", defaultValue = "") String movementType,
            Pageable pageable) {
        long startTime = System.currentTimeMillis();
        CustomPage<CashRegisterMinDTO> response = service.searchEntity(
                id, operatorId, status, cashMovementType, movementType, timeStart, timeEnd, pageable);
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;
        System.out.println("**************************************************************************************************************");
        System.out.println("TEMPO TOTAL: " + queryTime);
        System.out.println("**************************************************************************************************************");
        return ResponseEntity.ok(response);
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
    public ResponseEntity<CashClosingMinDTO> closeBalance(@Valid @RequestBody CashRegisterDTO dto) {
        long startTime = System.currentTimeMillis();
        CashClosingMinDTO minDTO = service.closingBalance(dto);
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
