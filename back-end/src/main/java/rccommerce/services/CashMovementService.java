package rccommerce.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashMovementMinDTO;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.PaymentType;
import rccommerce.repositories.CashMovimentRepository;
import rccommerce.repositories.CashRegisterRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;

public class CashMovementService implements GenericService<CashMovement, CashMovementDTO, CashMovementMinDTO, Long> {

    @Autowired
    private CashMovimentRepository repository;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public JpaRepository<CashMovement, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(CashMovementDTO dto, CashMovement entity) {
        entity.setCashMovementType(CashMovementType.fromValue(dto.getCashMovementType()));
        entity.setPaymentType(PaymentType.fromValue(dto.getPaymentType()));
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        entity.setTimestamp(dto.getTimestamp());

        // Validação e associação do CashRegister
        CashRegister cashRegister = cashRegisterRepository.findById(dto.getCashRegisterId())
                .orElseThrow(() -> new ResourceNotFoundException("Caixa não encontrado para o ID: " + dto.getCashRegisterId()));
        entity.setCashRegister(cashRegister);
    }

    @Override
    public CashMovement createEntity() {
        return new CashMovement();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.CashMovement", null, Locale.getDefault());
    }

}
