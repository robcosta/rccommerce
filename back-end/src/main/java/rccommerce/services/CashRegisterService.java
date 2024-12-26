package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
import rccommerce.dto.CashReportMinDTO;
import rccommerce.dto.MovementDetailDTO;
import rccommerce.entities.CashMovement;
import rccommerce.entities.CashRegister;
import rccommerce.entities.MovementDetail;
import rccommerce.entities.Operator;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.entities.enums.MovementType;
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

    /*
     * Realiza a abertura do caixa, lançando uma exceção caso o 
     * operador já possua um caixa aberto.
     */
    @Transactional
    public CashRegisterMinDTO openBalance(CashRegisterDTO dto) {
        if (hasInvalidMovementType(dto, MovementType.MONEY)) {
            throw new InvalidArgumentExecption("A abertura de caixa só pode conter movimentos do tipo 'MONEY'.");
        }

        // Verifica se o operador já possui um caixa aberto
        validateNoOpenCashRegister();

        // Cria um novo caixa para o operador atual
        return GenericService.super.insert(dto);
    }

    /*
     * Realiza o fechamento do caixa comparando os dados enviados pelo usuário
     * com os dados do sistema.
     */
    @Transactional
    public CashReportMinDTO closingBalance(CashRegisterDTO dto) {
        CashRegister cashRegister = validateOpenCashRegister();
        BigDecimal totalAmount = cashRegister.getTotalAmount();

        // Pega a movimentação do caixa antes do fechamento
        Map<MovementType, BigDecimal> movements = cashRegister.getMovementTotals();

        // Compara os valores enviados pelo usuário com os do sistema
        boolean hasDifference = !validateMovementTotals(dto, cashRegister);

        if (hasDifference && !dto.isForceClose()) {
            throw new MessageToUsersException("Valores informados diferem dos valores do sistema.");
        }

        // Atualiza o caixa com os dados de fechamento enviados pelo operador
        cashRegister.setCloseTime(Instant.now());
        CashRegisterMinDTO cashRegisterMinDTO = GenericService.super.update(dto, cashRegister.getId());

        BigDecimal difference = dto.getTotalAmount().subtract(totalAmount);

        CashReportMinDTO cashReportMinDTO = CashReportMinDTO.builder()
                .operator(CashReportMinDTO.OperatorReporter.builder()
                        .id(cashRegister.getOperator().getId())
                        .name(cashRegister.getOperator().getName())
                        .build())
                .openTime(cashRegisterMinDTO.getOpenTime())
                .closeTime(cashRegisterMinDTO.getCloseTime())
                .operatorData(dto.getMovementTotals())
                .systemData(movements)
                .amountOperator(dto.getTotalAmount())
                .amountSystem(totalAmount)
                .difference(difference)
                .build();

        return cashReportMinDTO;
    }

    // /**
    //  * Retorna o caixa aberto do operador atual. Lança exceção se não houver um
    //  * caixa aberto.
    //  */
    // @Transactional(readOnly = true)
    // public CashRegister getOpenCashRegisterByOperator() {
    //     List<CashRegister> cashRegisters = repository.findByOperatorId(getOperatorId());
    //     return cashRegisters.stream()
    //             .filter(cashRegister -> cashRegister.getCloseTime() == null)
    //             .findFirst()
    //             .orElseThrow(() -> new InvalidArgumentExecption("O operador não possui um caixa aberto."));
    // }
    /**
     * Valida se o operador já possui um caixa aberto. Lança exceção se um caixa
     * estiver aberto.
     */
    @Transactional(readOnly = true)
    public void validateNoOpenCashRegister() {
        List<CashRegister> cashRegisters = repository.findByOperatorId(getOperatorId());
        boolean hasOpenCashRegister = cashRegisters.stream()
                .anyMatch(cashRegister -> cashRegister.getCloseTime() == null);
        if (hasOpenCashRegister) {
            throw new InvalidArgumentExecption("O operador já possui um caixa aberto.");
        }
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
        List<CashRegister> cashRegisters = repository.findByOperatorId(getOperatorId());
        return cashRegisters.stream()
                .filter(cashRegister -> cashRegister.getCloseTime() == null)
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentExecption("O operador não possui um caixa aberto."));
    }

    // /**
    //  * Valida se o operador já possui um caixa aberto ou não *
    //  */
    // @Transactional(readOnly = true)
    // public boolean hasOpenCashRegister() {
    //     List<CashRegister> cashRegisters = repository.findByOperatorId(getOperatorId());
    //     return cashRegisters.stream().anyMatch(cashRegister -> cashRegister.getCloseTime() == null);
    // }
    public Long getOperatorId() {
        return SecurityContextUtil.getUserId();
    }

    private boolean hasInvalidMovementType(CashRegisterDTO dto, MovementType movementType) {
        return dto.getCashMovements().stream()
                .flatMap(cashMovementDTO -> cashMovementDTO.getMovementDetails().stream())
                .anyMatch(detail -> detail.getMovementType() != movementType);
    }

    @Override
    public JpaRepository<CashRegister, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(CashRegisterDTO dto, CashRegister entity) {
        // BigDecimal totalAmount = new BigDecimal(0);
        entity.getCashMovements().clear();
        for (CashMovementDTO cashMovementDTDO : dto.getCashMovements()) {
            CashMovement cashMovement = new CashMovement();
            CashMovementType movementType = CashMovementType.valueOf(cashMovementDTDO.getCashMovementType());
            cashMovement.setCashMovementType(movementType);
            cashMovement.setDescription(movementType.getDescription());
            cashMovement.setTimestamp(Instant.now());
            //cashMovement.getMovementDetails().clear();
            for (MovementDetailDTO movementDetailDTO : cashMovementDTDO.getMovementDetails()) {
                MovementDetail movementDetail = new MovementDetail();
                movementDetail.setMovementType(movementDetailDTO.getMovementType());
                movementDetail.setAmount(movementType.applyFactor(movementDetailDTO.getAmount()));
                movementDetail.setPayment(null);
                // Adiciona MovementDetail ao CashMovement respeitando o relacionamento bidirecional         
                cashMovement.addMovementDetail(movementDetail);
            }
            // Adicona CashMovement ao CashRegiste respeitando o relacionamento bidirecional
            entity.addMovement(cashMovement);
            // totalAmount = totalAmount.add(cashMovement.getTotalAmount());
        }
        // entity.setBalance(totalAmount);
    }

    public boolean validateMovementTotals(CashRegisterDTO dto, CashRegister entity) {

        // Calcula os totais por MovementType no CashRegister
        Map<MovementType, BigDecimal> entityTotals = entity.getMovementTotals();

        // Obtém os totais por MovementType fornecidos no DTO
        Map<MovementType, BigDecimal> dtoTotals = dto.getMovementTotals();

        // Verifica se os valores de cada MovementType no DTO são iguais aos do CashRegister
        return entityTotals.equals(dtoTotals);
    }

    @Override
    public CashRegister createEntity() {
        Operator operator = operatorRepository.findById(getOperatorId()).get();
        CashRegister cashRegister = new CashRegister();
        cashRegister.setOperator(operator);
        cashRegister.setBalance(BigDecimal.ZERO);
        cashRegister.setOpenTime(Instant.now());
        return cashRegister;
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.CashRegister", null, Locale.getDefault());
    }
}
