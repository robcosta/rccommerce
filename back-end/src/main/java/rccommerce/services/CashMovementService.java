package rccommerce.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashMovementMinDTO;
import rccommerce.entities.CashMovement;
import rccommerce.repositories.CashMovimentRepository;
import rccommerce.services.interfaces.GenericService;

@Service
public class CashMovementService implements GenericService<CashMovement, CashMovementDTO, CashMovementMinDTO, Long> {

    @Autowired
    private CashMovimentRepository repository;

    @Autowired
    private CashRegisterService cashRegisterService;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public JpaRepository<CashMovement, Long> getRepository() {
        return repository;
    }

    // @Transactional
    // public CashMovementMinDTO openingBalance(CashMovementDTO dto) {
    //     if (!dto.getCashMovementType().equals(CashMovementType.OPENING_BALANCE.getName())) {
    //         throw new InvalidArgumentExecption("Movimento de caixa tem de ser do tipo 'OPENING_BALANCE'");
    //     }
    //     return registerBalance(dto);
    // }
    // public CashMovementMinDTO inputBalance(CashMovementDTO dto) {
    //     return registerBalance(dto);
    // }
    // @Transactional
    // public CashReportMinDTO closeCashRegister(CashMovementDTO dto) {
    //     CashRegister cashRegister = cashRegisterService.getOpenCashRegisterByOperator();
    //     CashMovementMinDTO cashMovement = registerBalance(dto);
    //     BigDecimal amountOperator = dto.getTotalAmount();
    //     BigDecimal amountSystem = cashMovement.getTotalAmount();
    //     BigDecimal difference = amountOperator.subtract(amountSystem);
    //     if (!difference.equals(BigDecimal.ZERO)) {
    //         throw new MessageToUsersException("Diferença no caixa, deseja continuar?");
    //     }
    //     return CashReportMinDTO.builder()
    //             .operator(new OperatorMinDTO(cashRegister.getOperator()))
    //             .openTime(cashRegister.getOpenTime())
    //             .closeTime(cashRegister.getCloseTime())
    //             .operatorData(dto.getMovementDetails())
    //             .systemData(dto.getMovementDetails())
    //             .amountOperator(amountOperator)
    //             .amountSystem(amountSystem)
    //             .difference(amountOperator.subtract(amountSystem))
    //             .build();
    //     // dto.setCashMovementType(CashMovementType.CLOSING_BALANCE);
    //     // // Envia a totalização informada pelo operador para proceder o fechamento do caixa
    //     // List<CashMovement> cashMovement = registerBalance(dto);
    //     // // Totaliza os valores do caixa enviados pelo operador
    //     // BigDecimal totalExpectedAmount = dto.getTotalAmount();
    //     // // Operator operator = operatorRepository.getReferenceById(cashMovement.getCashRegister().getOperator().getId());  // Obtém o operador
    //     // CashReportDTO cashReportDTO = CashReportDTO.builder()
    //     //         .operator(new OperatorDTO(cashMovement.getCashRegister().getOperator()))
    //     //         .openTime(cashMovement.getCashRegister().getOpenTime())
    //     //         .closeTime(cashMovement.getCashRegister().getCloseTime())
    //     //         //  .operatorData(dto.getPaymentTypeAmounts()) //Dados enviados pelo operador
    //     //         .systemData(cashMovement.getMovementType()) // Dados do sistema
    //     //         .closingBalance(cashMovement.getCashRegister().getBalance())
    //     //         .difference(cashMovement.getCashRegister().getBalance())
    //     //         .build();
    // }
    // @Transactional
    // private CashMovementMinDTO registerBalance(CashMovementDTO dto) {
    //     CashRegister cashRegister = cashRegisterService.getCashRegister(dto.getTotalAmount(), CashMovementType.fromValue(dto.getCashMovementType()));
    //     // CashRegisterDTO cashRegisterDTO = new CashRegisterDTO(cashRegister);
    //     // dto.setCashRegisterDTO(cashRegisterDTO);
    //     return insert(dto);
    // }
    // @Override
    // public void copyDtoToEntity(CashMovementDTO dto, CashMovement entity) {
    //     entity.setId(dto.getId());
    //     entity.setCashMovementType(CashMovementType.fromValue(dto.getCashMovementType()));
    //     // Limpa os detalhes existentes
    //     entity.getMovementDetails().clear();
    //     MovementDetail movementDetail;
    //     for (MovementDetailDTO movementDetailDTO : dto.getMovementDetails()) {
    //         movementDetail = MovementDetail.builder()
    //                 .id(movementDetailDTO.getId())
    //                 .movementType(movementDetailDTO.getMovementType())
    //                 .amount(movementDetailDTO.getAmount())
    //                 .cashMovement(entity)
    //                 .build();
    //         entity.addMovementDetail(movementDetail);
    //     }
    //     entity.setDescription(CashMovementType.fromValue(dto.getCashMovementType()).getDescription());
    //     entity.setTimestamp(Instant.now());
    //     CashRegister cashRegister = cashRegisterService.getOpenCashRegisterByOperator();
    //     entity.setCashRegister(cashRegister);
    // }
    @Override
    public CashMovement createEntity() {
        return new CashMovement();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.CashMovement", null, Locale.getDefault());
    }

    @Override
    public void copyDtoToEntity(CashMovementDTO dto, CashMovement entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'copyDtoToEntity'");
    }

    // private CashReportDTO proceedWithCashRegisterClosing(CashRegister cashRegister, CashClosingDTO dto, BigDecimal difference) {
    //     // Criar o movimento de fechamento de caixa
    //     CashMovement cashMovement = new CashMovement();
    //     cashMovement.setAmount(dto.getPaymentTypes().values().stream()
    //             .reduce(BigDecimal.ZERO, BigDecimal::add));  // Somando os valores dos pagamentos informados
    //     cashMovement.setTimestamp(Instant.now());
    //     cashMovement.setCashMovementType(CashMovementType.CLOSING_BALANCE);
    //     cashMovement.setPaymentType(MovementType.MONEY);  // Tipo de pagamento (ajustar conforme a lógica de seu sistema)
    //     cashMovement.setDescription("Fechamento de caixa");
    //     // Adicionar o movimento de fechamento ao caixa
    //     cashRegister.addMovement(cashMovement);
    //     // Atualizar o saldo de fechamento do caixa
    //     cashRegister.setBalance(cashRegister.getBalance().subtract(cashMovement.getAmount()));
    //     // Atualizar o status do caixa para fechado
    //     cashRegister.setClosed(true);
    //     cashRegister.setCloseTime(Instant.now());
    //     // Salvar o movimento de caixa e o caixa
    //     cashMovement = cashMovementRepository.saveAndFlush(cashMovement);
    //     cashRegisterService.save(cashRegister);
    //     // Preparar o DTO de retorno
    //     OperatorDTO operatorDTO = operatorService.getOperatorDTO(cashRegister.getOperator());  // Obtém o operador
    //     CashReportDTO cashReportDTO = CashReportDTO.builder()
    //             .operator(operatorDTO)
    //             .openTime(cashRegister.getOpenTime())
    //             .closeTime(cashRegister.getCloseTime())
    //             .operatorData(dto.getPaymentTypes()) // Dados enviados pelo operador
    //             .systemData(persistedAmounts) // Dados do sistema
    //             .closingBalance(cashRegister.getBalance())
    //             .difference(difference)
    //             .build();
    //     return cashReportDTO;
    // }
    // @Transactional
    // private CashMovement registerBalance(BigDecimal amount, CashMovementType cashMovementType) {
    //     //    Long operatorId = SecurityContextUtil.getUserId();
    //     CashRegister cashRegister = cashRegisterService.getCashRegister(amount, cashMovementType);
    //     CashRegisterDTO cashRegisterDTO;
    //     Map<MovementType, BigDecimal> persistedAmounts;
    //     switch (cashMovementType) {
    //         // case OPENING_BALANCE -> {
    //         //     cashRegister = openingBalance(amount);
    //         //     cashRegisterDTO = new CashRegisterDTO(cashRegister);
    //         // }
    //         case CLOSING_BALANCE -> {
    //             cashRegister = closingBalance(amount);
    //             cashRegisterDTO = new CashRegisterDTO(cashRegister);
    //             // Obter os valores agrupados por MovementType dos movimentos de caixa persistidos no banco
    //             persistedAmounts = repository.sumAmountsByPaymentType(cashRegister.getId());
    //             BigDecimal totalSystemAmount = persistedAmounts.values().stream()
    //                     .reduce(BigDecimal.ZERO, BigDecimal::add);
    //             // Verificar a diferença entre o valor informado pelo operador e o valor infroamdo pelo sistema
    //             BigDecimal difference = amount.subtract(totalSystemAmount);
    //             // Se houver diferença, perguntar ao operador se deseja prosseguir com o fechamento
    //             if (difference.compareTo(BigDecimal.ZERO) != 0) {
    //                 // Se houver diferença, verificar com o operador se deseja continuar
    //                 // (aqui podemos lançar uma exceção ou enviar uma mensagem para o operador)
    //                 if (!askOperatorIfProceedWithDifference(difference)) {
    //                     // Se o operador não desejar continuar, lançar uma exceção ou retornar um erro
    //                     throw new MessageToUsersException("O operador optou por refazer o fechamento.");
    //                 }
    //             }
    //         }
    //         case SALE, REINFORCEMENT, DIVERSE_RECEIPT, INTEREST_OR_FINE, OTHER_RECEIPTS -> {
    //             cashRegister = getCashRegister();
    //             cashRegister.addToBalance(amount);
    //             cashRegisterDTO = new CashRegisterDTO(cashRegister);
    //         }
    //         // case WITHDRAWAL, INITIAL_CHANGE, OPERATIONAL_EXPENSE, REIMBURSEMENT, DISCOUNT, OTHER_EXPENSES -> {
    //         //     System.out.println("OTHER_RECEIPTS");
    //         // }
    //         default ->
    //             throw new InvalidArgumentExecption("Movimento de caixa inválido: " + cashMovementType.getName());
    //     }
    //     // Cria o movimento de caixa
    //     CashMovement entity = createEntity();
    //     entity.setAmount(amount);
    //     entity.setTimestamp(Instant.now());
    //     entity.setCashMovementType(cashMovementType);
    //     entity.setPaymentType(MovementType.MONEY);
    //     entity.setDescription(cashMovementType.getDescription());
    //     entity.setCashRegister(cashRegister);
    //     try {
    //         // Salva o movimento e caixa
    //         if (cashRegisterDTO.getId() == null) {
    //             cashRegisterService.insert(cashRegisterDTO);
    //         } else {
    //             cashRegisterService.update(cashRegisterDTO, cashRegisterDTO.getId());
    //         }
    //         entity = getRepository().save(entity);
    //         return entity;
    //     } catch (DataIntegrityViolationException e) {
    //         handleDataIntegrityViolation(e);
    //         return null;
    //     }
    // }
}
