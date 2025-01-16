package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CashClosingMinDTO;
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
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.repositories.CashRegisterRepository;
import rccommerce.repositories.OperatorRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.MessageToUsersException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.DateUtils;
import rccommerce.services.util.SecurityContextUtil;
import rccommerce.util.CustomPage;

@Service
public class CashRegisterService implements GenericService<CashRegister, CashRegisterDTO, CashRegisterMinDTO, Long> {

    @Autowired
    private CashRegisterRepository repository;

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Busca caixa conforme parâmetros enviados pelo usuário: id - id do caixa
     * operatorID - id do operador status - Null - todos os caixas; OPEN - busca
     * caixas abertos; CLOSED - busca caixas fechados. cashMovementType - Tipo
     * de movimentação do caixa (SALE, OPENING_BALANCE, CLOSING_BALANCE, etc)
     * movementType - Tipo de movimentação (MONEY, PIX, CREDIT_CARD, etc)
     * timeStart - Início do período para busca timeEnd - Período final para
     * busca
     *
     * @return Pagina customizada de CashRegisterMinDTO
     *
     * @throws ResourceNotFoundException, caso não encontre nenhum caixacom as
     * especificações passdas
     * @throws InvalidArgumentExecption se o status, cahMovementType ou
     * movementType forem inválidos.
     */
    @Transactional(readOnly = true)
    public CustomPage<CashRegisterMinDTO> searchEntity(
            String id, String operatorId, String status, String cashMovementType,
            String movementType, String timeStart, String timeEnd, Pageable pageable) {
        // Conversão de id e operatorId para Long
        Long cashRegisterId = parseLongOrNull(id);
        Long operator = parseLongOrNull(operatorId);

        // Conversão da string de data para Instant
        Instant openTimeStart = parseDateOrNull(timeStart);
        Instant openTimeEnd = parseDateOrNull(timeEnd);

        // Validação e normalização do status
        Boolean isOpen = normalizeStatus(status);

        // Realiza o primeiro filtro da consulta junto ao banco de dados.
        // Convertertendo o conteúdo da página retornada pelo repositório em um conjunto (Set).
        // A chamada ao método repository.findCashRegister(...) retorna um objeto Page<CashRegister>,
        // que contém os registros de caixa paginados. O método getContent() extrai a lista de elementos
        // contidos na página. Essa lista é então convertida em um HashSet para garantir que os elementos
        // sejam únicos (sem duplicados).
        Set<CashRegister> cashRegisterSet = new HashSet<>(repository.findCashRegister(cashRegisterId, operator, isOpen, openTimeStart, openTimeEnd, pageable).getContent());

        // Realiza o segundo filtro da consulta junto aos objetos em memória trazidos pelo primeiro filtro.
        // Cria um DTO (Data Transfer Object) CashReportMinDTO utilizando o conjunto de registros de caixa 
        // obtido e os filtros adicionais fornecidos (cashMovementType e movementType). 
        // Esse DTO é responsável por armazenar os dados processados para serem enviados como resposta.
        CashReportMinDTO cashReportMinDTO = createCashReportDTO(cashRegisterSet, cashMovementType, movementType);

        // Paginação manual
        Set<CashRegisterMinDTO> cashRegisters = cashReportMinDTO.getCashRegisters();

        // Segunda verificação: Verifica se algum registro na memória passou no segundo filtro, 
        // se retornar vazio lança uma exceção informando que não há registros que atendam aos critérios.
        if (cashRegisterSet.isEmpty() || cashRegisters.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum registro de caixa encontrado para os critérios especificados.");
        }
        List<CashRegisterMinDTO> cashRegisterList = new ArrayList<>(cashRegisters);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), cashRegisterList.size());
        // Verifica se o índice 'start' está fora dos limites da lista
        if (start > cashRegisterList.size()) {
            start = cashRegisterList.size();
        }
        // Garante que 'start' nunca será maior que 'end'
        if (start > end) {
            start = end;
        }

        List<CashRegisterMinDTO> paginatedList = cashRegisterList.subList(start, end);

        // Calcula a totalização
        Map<MovementType, BigDecimal> totalizationCash = cashReportMinDTO.getTotalizationCash();

        // Retorna o wrapper com a página e a totalização
        return new CustomPage<>(
                paginatedList,
                totalizationCash,
                pageable,
                pageable.getPageNumber() == (int) Math.ceil((double) cashRegisters.size() / pageable.getPageSize()) - 1, // last
                (int) Math.ceil((double) cashRegisters.size() / pageable.getPageSize()), // totalPages
                cashRegisters.size(), // totalElements
                pageable.getPageSize(), // size
                pageable.getPageNumber(), // number
                pageable.getSort(), // sort
                pageable.getPageNumber() == 0, // first
                paginatedList.size(), // numberOfElements
                paginatedList.isEmpty() // empty
        );
    }

    /*
     * Realiza a abertura do caixa, lançando uma exceção caso o 
     * operador já possua um caixa aberto.
     */
    @Transactional
    public CashRegisterMinDTO openBalance(CashRegisterDTO dto) {
        checkUserPermissions(PermissionAuthority.PERMISSION_CASH);

        // Certifica que a abertura de caixa só contém movimentos do tipo 'MONEY'
        if (hasInvalidMovementType(dto, MovementType.MONEY)) {
            throw new InvalidArgumentExecption("A abertura de caixa só pode conter movimentos do tipo 'MONEY'.");
        }

        // Verifica se o operador já possui um caixa aberto
        validateNoOpenCashRegister();

        // Cria um novo caixa para o operador atual
        return GenericService.super.insert(dto, false);
    }

    /*
     * Realiza o fechamento do caixa comparando os dados enviados pelo usuário
     * com os dados do sistema.
     */
    @Transactional
    public CashClosingMinDTO closingBalance(CashRegisterDTO dto) {
        checkUserPermissions(PermissionAuthority.PERMISSION_CASH);

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
        cashRegister.setCloseTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        CashRegisterMinDTO cashRegisterMinDTO = update(dto, cashRegister.getId(), false);

        BigDecimal difference = dto.getTotalAmount().subtract(totalAmount);

        CashClosingMinDTO cashClosedMinDTO = CashClosingMinDTO.builder()
                .operator(CashClosingMinDTO.OperatorReporter.builder()
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

        return cashClosedMinDTO;
    }

    /*
     * Realiza a movimentação de valores do caixa como: Retirada, Depósito, etc.
     */
    @Transactional
    public CashRegisterMinDTO registerBalance(CashRegisterDTO dto) {
        checkUserPermissions(PermissionAuthority.PERMISSION_CASH);

        if (hasInvalidMovementType(dto, MovementType.MONEY)) {
            throw new InvalidArgumentExecption("Utilize apenas dinheiro para operar com reforço ou retirarada do caixa.");
        }
        CashRegister entity = validateOpenCashRegister();

        // Verificar se existe dinheiro suficiente no caixa para realizar algum tipo de retirada
        Boolean cashExits = dto.getCashMovements().stream()
                .allMatch(cashMovement -> CashMovementType.fromValue(cashMovement.getCashMovementType())
                .getFactor()
                .compareTo(BigDecimal.ZERO) < 0);

        if (cashExits && dto.getTotalMoneyPayments().compareTo(entity.totalMoney()) > 0) {
            throw new InvalidArgumentExecption("Não existe dinheiro suficiente em caixa para realizar a operação.");
        }

        return update(dto, entity.getId(), false);
    }

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

    @Override
    public JpaRepository<CashRegister, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(CashRegisterDTO dto, CashRegister entity) {
        entity.getCashMovements().clear();
        for (CashMovementDTO cashMovementDTDO : dto.getCashMovements()) {
            CashMovementType movementType = CashMovementType.valueOf(cashMovementDTDO.getCashMovementType());
            CashMovement cashMovement = CashMovement.builder()
                    .cashMovementType(movementType)
                    .description(movementType.getDescription() + " - Usuário:" + getOperatorId())
                    .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                    .build();
            for (MovementDetailDTO movementDetailDTO : cashMovementDTDO.getMovementDetails()) {
                cashMovement.addMovementDetail(MovementDetail.builder()
                        .movementType(movementDetailDTO.getMovementType())
                        .amount(movementType.applyFactor(movementDetailDTO.getAmount()))
                        .payment(null)
                        .build()
                );
            }
            // Adicona CashMovement ao CashRegiste respeitando o relacionamento bidirecional
            entity.addMovement(cashMovement);
        }
    }

    @Override
    public CashRegister createEntity() {
        Operator operator = operatorRepository.findById(getOperatorId()).get();
        CashRegister cashRegister = new CashRegister();
        cashRegister.setOperator(operator);
        cashRegister.setBalance(BigDecimal.ZERO);
        cashRegister.setOpenTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        return cashRegister;
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.CashRegister", null, Locale.getDefault());
    }

    // ******************** MÉTODOS PRIVADOS AUXILIARES **********************
    /**
     * Cria o CashReportMinDTO filtrando por CashMovementType e o MovementType
     */
    private CashReportMinDTO createCashReportDTO(Set<CashRegister> cashRegisterSet, String cashMovementType, String movementType) {
        boolean hasCashMovementType = cashMovementType != null && !cashMovementType.isEmpty();
        boolean hasMovementType = movementType != null && !movementType.isEmpty();

        if (hasCashMovementType && hasMovementType) {
            return new CashReportMinDTO(
                    cashRegisterSet,
                    CashMovementType.fromValue(cashMovementType),
                    MovementType.fromValue(movementType)
            );
        }
        if (hasCashMovementType) {
            return new CashReportMinDTO(cashRegisterSet, CashMovementType.fromValue(cashMovementType));
        }
        if (hasMovementType) {
            return new CashReportMinDTO(cashRegisterSet, MovementType.fromValue(movementType));
        }
        return new CashReportMinDTO(cashRegisterSet);
    }

    /**
     * Verifica se os dados de fechamento do caixa enviados pelo operador são
     * iguais aos dados do sistema.
     */
    private boolean validateMovementTotals(CashRegisterDTO dto, CashRegister entity) {

        // Calcula os totais por MovementType no CashRegister
        Map<MovementType, BigDecimal> entityTotals = entity.getMovementTotals();

        // Obtém os totais por MovementType fornecidos no DTO
        Map<MovementType, BigDecimal> dtoTotals = dto.getMovementTotals();

        // Verifica se os valores de cada MovementType no DTO são iguais aos do CashRegister
        return entityTotals.equals(dtoTotals);
    }

    /**
     * Verifica se o tipo de movimento é diferente do esperado.
     */
    private boolean hasInvalidMovementType(CashRegisterDTO dto, MovementType movementType) {
        return dto.getCashMovements().stream()
                .flatMap(cashMovementDTO -> cashMovementDTO.getMovementDetails().stream())
                .anyMatch(detail -> detail.getMovementType() != movementType);
    }

    /**
     * Converte uma string para Long ou retorna null se a string for inválida.
     */
    private Long parseLongOrNull(String value) {
        return (value != null && !value.isEmpty()) ? Long.valueOf(value) : null;
    }

    /**
     * Converte uma string de data para Instant ou retorna null se a string for
     * inválida.
     */
    private Instant parseDateOrNull(String date) {
        return (date != null && !date.isEmpty()) ? DateUtils.convertDay(date, "dd/MM/yyyy") : null;
    }

    /**
     * Normaliza o status recebido como string e retorna o correspondente
     * Boolean.
     *
     * @throws InvalidArgumentExecption se o status for inválido.
     */
    private Boolean normalizeStatus(String status) {
        status = (status == null || status.isEmpty()) ? "ALL" : status.trim().toUpperCase();

        return switch (status) {
            case "OPEN" ->
                true;
            case "CLOSED" ->
                false;
            case "ALL" ->
                null;
            default ->
                throw new InvalidArgumentExecption("Status inválido. Use 'OPEN', 'CLOSED' ou 'ALL'.");
        };
    }

    /**
     * Retorna o id do operador logado na seção. Boolean.
     */
    private Long getOperatorId() {
        return SecurityContextUtil.getUserId();
    }
}
