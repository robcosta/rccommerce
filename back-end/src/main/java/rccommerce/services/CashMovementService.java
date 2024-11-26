package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashMovementBalanceDTO;
import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashMovementMinDTO;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.PaymentType;
import rccommerce.repositories.CashMovimentRepository;
import rccommerce.services.interfaces.GenericService;

@Service
public class CashMovementService implements GenericService<CashMovement, CashMovementDTO, CashMovementMinDTO, Long> {

    @Autowired
    private CashMovimentRepository repository;

    @Autowired
    private CashRegisterService cashRegisterService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public JpaRepository<CashMovement, Long> getRepository() {
        return repository;
    }

    public CashMovementMinDTO openingBalance(CashMovementBalanceDTO dto) {
        return registerBalance(dto.getAmount(), CashMovementType.OPENING_BALANCE);
    }

    @Transactional
    public CashMovementMinDTO registerBalance(BigDecimal amount, CashMovementType cashMovementType) {
        //    Long operatorId = SecurityContextUtil.getUserId();
        CashRegister cashRegister;

        if (cashMovementType == CashMovementType.OPENING_BALANCE) {
            // Valida que o operador não tenha um caixa aberto
            cashRegisterService.validateNoOpenCashRegister();

            // Cria um novo caixa
            cashRegister = cashRegisterService.createNewCashRegister(amount);
        } else {
            // Obtém o caixa aberto do operador
            cashRegister = cashRegisterService.getOpenCashRegisterByOperator();
        }

        // Cria o movimento de caixa
        CashMovement entity = createEntity();
        entity.setAmount(amount);
        entity.setTimestamp(Instant.now());
        entity.setCashMovementType(cashMovementType);
        entity.setPaymentType(PaymentType.MONEY);
        entity.setDescription(cashMovementType.getDescription());
        entity.setCashRegister(cashRegister);

        try {
            // Salva o movimento
            entity = getRepository().saveAndFlush(entity);
            return entity.convertMinDTO();
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }
    }

    @Override
    public void copyDtoToEntity(CashMovementDTO dto, CashMovement entity) {
        // entity.setCashMovementType(CashMovementType.fromValue(dto.getCashMovementType()));
        // entity.setPaymentType(PaymentType.fromValue(dto.getPaymentType()));
        // entity.setAmount(dto.getAmount());
        // entity.setDescription(dto.getDescription());
        // entity.setTimestamp(dto.getTimestamp());

        // // Validação e associação do CashRegister
        // CashRegisterMinDTO cashRegister = cashRegisterService.findById(dto.getCashRegisterId());
        // if (cashRegister == null) {
        //     throw new ResourceNotFoundException("Caixa não encontrado para o ID: " + dto.getCashRegisterId());
        // }
        // //         .orElseThrow(() -> new ResourceNotFoundException("Caixa não encontrado para o ID: " + dto.getCashRegisterId()));
        // // entity.setCashRegister(cashRegister);
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
