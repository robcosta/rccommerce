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

@Entity
@Table(name = "tb_cash_register")
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant openTime;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant closeTime;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", unique = true, nullable = false)
    private Operator operator;

    @OneToMany(mappedBy = "cashRegister", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CashMovement> movements = new ArrayList<>();

    public CashRegister() {
        this.balance = BigDecimal.ZERO;
    }

    public CashRegister(Operator operator) {
        this();
        if (operator == null) {
            throw new IllegalArgumentException("Um operador deve ser associado ao caixa.");
        }
        this.operator = operator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Instant openTime) {
        this.openTime = openTime;
    }

    public Instant getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Instant closeTime) {
        this.closeTime = closeTime;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        if (operator == null) {
            throw new IllegalArgumentException("Um operador deve ser associado ao caixa.");
        }
        this.operator = operator;
    }

    public List<CashMovement> getMovements() {
        return movements;
    }

    public void addMovement(CashMovement movement) {
        if (movement.getType().toString().contains("WITHDRAWAL")
                || movement.getType().toString().contains("EXPENSE")) {
            subtractFromBalance(movement.getAmount());
        } else {
            if (movement.getPaymentType() == null) {
                throw new IllegalArgumentException("Movimentos de entrada devem conter um tipo de pagamento.");
            }
            addToBalance(movement.getAmount());
        }
        movements.add(movement);
        movement.setCashRegister(this); // Relacionamento bidirecional
    }

    private void addToBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }

    private void subtractFromBalance(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente no caixa.");
        }
        balance = balance.subtract(amount);
    }
}
