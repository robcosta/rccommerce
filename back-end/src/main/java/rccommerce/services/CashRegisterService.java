package rccommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;
import rccommerce.entities.Operator;
import rccommerce.repositories.CashRegisterRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

public class CashRegisterService implements GenericService<CashRegister, CashRegisterDTO, CashRegisterMinDTO, Long> {

    @Autowired
    private CashRegisterRepository repository;

    @Autowired
    private CashMovementService cashMovementService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public JpaRepository<CashRegister, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(CashRegisterDTO dto, CashRegister entity) {
        Operator operator = new Operator();
        List<CashMovement> movements = new ArrayList<>();

        operator.setId(SecurityContextUtil.getUserId());

        entity.setBalance(dto.getBalance());
        entity.setOpenTime(dto.getOpenTime());
        entity.setCloseTime(dto.getCloseTime());
        entity.setOperator(operator);
        for (CashMovementDTO cashMovementDTO : dto.getMovements()) {
            CashMovement cashMovement = new CashMovement();
            cashMovementService.copyDtoToEntity(cashMovementDTO, cashMovement);
            movements.add(cashMovement);
        }
        entity.setMovements(movements);
    }

    @Override
    public CashRegister createEntity() {
        return new CashRegister();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.CashRegister", null, Locale.getDefault());
    }

}
