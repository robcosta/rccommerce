package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rccommerce.dto.CashRegisterDTO;
import rccommerce.dto.CashRegisterMinDTO;
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

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "operator_id", unique = true, nullable = false)
    private Operator operator;
    // @JsonBackReference
    @OneToMany(mappedBy = "cashRegister", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)

    private List<CashMovement> cashMovements = new ArrayList<>();

    public CashRegister() {
        this.balance = BigDecimal.ZERO;
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

    public List<CashMovement> getMovements() {
        return cashMovements;
    }

    public void addMovement(CashMovement movement) {

        if (movement.getMovementDetails() == null) {
            throw new InvalidArgumentExecption("Movimentos de entrada devem conter um tipo de pagamento.");
        }
        cashMovements = new ArrayList<>();
        // Verifica o tipo do movimento
        switch (movement.getCashMovementType()) {
            case WITHDRAWAL, OPERATIONAL_EXPENSE, REIMBURSEMENT, DISCOUNT, OTHER_EXPENSES, CLOSING_BALANCE -> {
                subtractFromBalance(movement.getTotalAmount());
            }
            case SALE, REINFORCEMENT, DIVERSE_RECEIPT, INTEREST_OR_FINE, OTHER_RECEIPTS, OPENING_BALANCE -> {
                addToBalance(movement.getTotalAmount());
            }
            default ->
                throw new InvalidArgumentExecption("Movimento de caixa inválido: " + movement.getCashMovementType().getName());
        }
        // Relacionamento bidirecional
        movement.setCashRegister(this);
        // Adiciona o movimento ao caixa
        cashMovements.add(movement);
    }

    public void addToBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InvalidArgumentExecption("Saldo insuficiente no caixa.");
        }
        balance = balance.subtract(amount);
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
