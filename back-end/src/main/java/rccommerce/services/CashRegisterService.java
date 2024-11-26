package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
import rccommerce.entities.CashRegister;
import rccommerce.entities.Operator;
import rccommerce.repositories.CashRegisterRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class CashRegisterService implements GenericService<CashRegister, CashRegisterDTO, CashRegisterMinDTO, Long> {

    @Autowired
    private CashRegisterRepository repository;

    // @Autowired
    // private CashMovementService cashMovementService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Retorna o caixa aberto do operador atual. Lança exceção se não houver um
     * caixa aberto.
     */
    @Transactional(readOnly = true)
    public CashRegister getOpenCashRegisterByOperator() {
        return repository.findByOperatorId(getOperatorId())
                .filter(cashRegister -> cashRegister.getCloseTime() == null)
                .orElseThrow(() -> new InvalidArgumentExecption("Nenhum caixa aberto encontrado para o operador."));
    }

    /**
     * Cria um novo caixa para o operador atual.
     */
    @Transactional
    public CashRegister createNewCashRegister(BigDecimal amount) {
        Operator operator = new Operator();
        operator.setId(getOperatorId());

        CashRegister cashRegister = new CashRegister();
        cashRegister.setOperator(operator);
        cashRegister.setBalance(amount);
        cashRegister.setOpenTime(Instant.now());

        return repository.saveAndFlush(cashRegister);
    }

    /**
     * Valida se o operador já possui um caixa aberto. Lança exceção se um caixa
     * estiver aberto.
     */
    @Transactional(readOnly = true)
    public void validateNoOpenCashRegister() {
        repository.findByOperatorId(getOperatorId())
                .filter(cashRegister -> cashRegister.getCloseTime() == null)
                .ifPresent(cashRegister -> {
                    throw new InvalidArgumentExecption("O operador já possui um caixa aberto.");
                });
    }

    public Long getOperatorId() {
        return SecurityContextUtil.getUserId();
    }

    @Override
    public JpaRepository<CashRegister, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(CashRegisterDTO dto, CashRegister entity) {
        // Operator operator = new Operator();
        // List<CashMovement> movements = new ArrayList<>();

        // operator.setId(SecurityContextUtil.getUserId());
        // entity.setBalance(dto.getBalance());
        // entity.setOpenTime(dto.getOpenTime());
        // entity.setCloseTime(dto.getCloseTime());
        // entity.setOperator(operator);
        // // for (CashMovementDTO cashMovementDTO : dto.getMovements()) {
        // //     CashMovement cashMovement = new CashMovement();
        // //     cashMovementService.copyDtoToEntity(cashMovementDTO, cashMovement);
        // //     movements.add(cashMovement);
        // // }
        // entity.setMovements(movements);
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
