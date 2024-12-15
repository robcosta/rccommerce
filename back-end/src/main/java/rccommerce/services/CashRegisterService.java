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
import rccommerce.entities.enums.CashMovementType;
import rccommerce.repositories.CashRegisterRepository;
import rccommerce.repositories.OperatorRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.MessageToUsersException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class CashRegisterService implements GenericService<CashRegister, CashRegisterDTO, CashRegisterMinDTO, Long> {

    @Autowired
    private CashRegisterRepository repository;

    @Autowired
    private OperatorRepository operatorRepository;

    // @Autowired
    // private CashMovementService cashMovementService;
    @Autowired
    private MessageSource messageSource;

    public CashRegister getCashRegister(BigDecimal amount, CashMovementType cashMovementType) {
        CashRegister cashRegister;
        switch (cashMovementType) {
            case OPENING_BALANCE -> {
                // Valida que o operador não tenha um caixa aberto
                if (!hasOpenCashRegister()) {
                    // Cria um novo caixa
                    return createNewCashRegister(amount);
                } else {
                    throw new InvalidArgumentExecption("Operador já tem caixa aberto.");
                }
            }
            case CLOSING_BALANCE -> {
                cashRegister = getOpenCashRegisterByOperator();
                cashRegister.subtractFromBalance(amount);

                // Se houver diferença, perguntar ao operador se deseja prosseguir com o fechamento
                if (cashRegister.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                    // Se houver diferença, verificar com o operador se deseja continuar
                    // (aqui podemos lançar uma exceção ou enviar uma mensagem para o operador)
                    if (!askOperatorIfProceedWithDifference(cashRegister.getBalance())) {
                        // Se o operador não desejar continuar, lançar uma exceção ou retornar um erro
                        throw new MessageToUsersException("O operador optou por refazer o fechamento.");
                    }
                }
                return cashRegister;
            }
            case SALE, REINFORCEMENT, DIVERSE_RECEIPT, INTEREST_OR_FINE, OTHER_RECEIPTS -> {
                cashRegister = getOpenCashRegisterByOperator();
                cashRegister.addToBalance(amount);
                return cashRegister;
            }
            case WITHDRAWAL, INITIAL_CHANGE, OPERATIONAL_EXPENSE, REIMBURSEMENT, DISCOUNT, OTHER_EXPENSES -> {
                cashRegister = getOpenCashRegisterByOperator();
                cashRegister.subtractFromBalance(amount);
                return cashRegister;
            }
            default ->
                throw new InvalidArgumentExecption("Movimento de caixa inválido: " + cashMovementType.getName());
        }
    }

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
        Operator operator = operatorRepository.findById(getOperatorId()).get();
        CashRegister cashRegister = new CashRegister();
        cashRegister.setOperator(operator);
        cashRegister.setBalance(amount);
        cashRegister.setOpenTime(Instant.now());

        return repository.save(cashRegister);
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

    /**
     * Valida se existe um caixa aberto para o operador. Lança exceção se não
     * existir, ou retorna o caixa aberto.
     *
     * @return CashRegister - o caixa aberto do operador.
     * @throws InvalidArgumentExecption se o operador não possuir um caixa
     * aberto.
     */
    @Transactional(readOnly = true)
    public CashRegister validateOpenCashRegister() {
        return repository.findByOperatorId(getOperatorId())
                .filter(cashRegister -> cashRegister.getCloseTime() == null)
                .orElseThrow(() -> new InvalidArgumentExecption("O operador não possui um caixa aberto."));
    }

    /**
     * Valida se o operador já possui um caixa aberto ou não *
     */
    @Transactional(readOnly = true)
    public boolean hasOpenCashRegister() {
        return repository.findByOperatorId(getOperatorId())
                .filter(cashRegister -> cashRegister.getCloseTime() == null)
                .isPresent();
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

    private boolean askOperatorIfProceedWithDifference(BigDecimal difference) {
        // Perguntar ao operador se ele deseja prosseguir com a diferença
        // Em um ambiente real, isso poderia ser feito via uma interface ou API de chat/mensagem
        // Aqui vamos apenas retornar true para simular que o operador aceitou fechar com a diferença
        // Implementar a lógica para a interação com o operador (por exemplo, através de uma mensagem, notificação ou interface)
        return true;  // Supondo que o operador deseja continuar
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
