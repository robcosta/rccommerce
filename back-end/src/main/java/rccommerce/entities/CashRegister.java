package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
import rccommerce.entities.enums.MovementType;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.interfaces.Convertible;

@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_cash_register")
public class CashRegister implements Convertible<CashRegisterDTO, CashRegisterMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant openTime;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant closeTime;

    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @OneToMany(mappedBy = "cashRegister", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<CashMovement> cashMovements = new HashSet<>();

    public CashRegister() {
        this.balance = BigDecimal.ZERO;
        this.cashMovements = new HashSet<>();
    }

    public CashRegister(Operator operator) {
        this();
        if (operator == null) {
            throw new InvalidArgumentExecption("Um operador deve ser associado ao caixa.");
        }
        this.operator = operator;
    }

    public void setOperator(Operator operator) {
        if (operator == null) {
            throw new InvalidArgumentExecption("Um operador deve ser associado ao caixa.");
        }
        this.operator = operator;
    }

    public void addCashMovements(CashMovement cashMovement) {
        cashMovements.add(cashMovement);
    }

    public void addMovement(CashMovement movement) {
        if (movement.getMovementDetails() == null) {
            throw new InvalidArgumentExecption("Movimentos de entrada devem conter um tipo de pagamento.");
        }

        addToBalance(movement.getTotalAmount());

        // Relacionamento bidirecional
        movement.setCashRegister(this);
        // Adiciona o movimento ao caixa
        cashMovements.add(movement);
    }

    private void addToBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InvalidArgumentExecption("Saldo insuficiente no caixa.");
        }
        balance = balance.subtract(amount);
    }

    public void subtractFromBalanceFinal(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

// Retorna o total em dinheiro do caixa
    public BigDecimal totalMoney() {
        return cashMovements.stream()
                .flatMap(cashMovement -> cashMovement.getMovementDetails().stream()) // Obtém os detalhes de cada movimento
                .filter(movementDetail -> movementDetail.getMovementType().equals(MovementType.MONEY)) // Filtra apenas os do tipo MONEY
                .map(MovementDetail::getAmount) // Mapeia para o valor (BigDecimal) do movimento
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma os valores, começando do ZERO
    }

    public BigDecimal getTotalAmount() {
        return cashMovements.stream()
                .flatMap(cashMovement -> cashMovement.getMovementDetails().stream())
                .map(MovementDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Mapeia os MovementType para o relatório final do caixa   
    public Map<MovementType, BigDecimal> getMovementTotals() {
        Map<MovementType, BigDecimal> movementTotals = new EnumMap<>(MovementType.class);

        if (cashMovements != null) {
            cashMovements.stream()
                    .flatMap(cashMovement -> cashMovement.getMovementDetails().stream())
                    .forEach(detail -> {
                        MovementType type = detail.getMovementType();
                        BigDecimal amount = detail.getAmount();

                        // Adiciona o valor ao total acumulado ou inicializa se ainda não existe
                        movementTotals.merge(type, amount, BigDecimal::add);
                    });
        }

        // Remove entradas com total igual a zero
        movementTotals.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);

        return movementTotals;
    }

    @Override
    public CashRegisterDTO convertDTO() {
        return new CashRegisterDTO(this);
    }

    @Override
    public CashRegisterMinDTO convertMinDTO() {
        return new CashRegisterMinDTO(this);
    }
}
