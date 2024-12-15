package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.CashMovementDTO;
import rccommerce.dto.CashMovementMinDTO;
import rccommerce.entities.enums.CashMovementType;
import rccommerce.services.interfaces.Convertible;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_cash_movement")
public class CashMovement implements Convertible<CashMovementDTO, CashMovementMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CashMovementType cashMovementType;

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = true, length = 50)
    // private MovimentType movementType;
    // @Column(nullable = false, precision = 15, scale = 2)
    // private BigDecimal amount;
    @Builder.Default
    @OneToMany(mappedBy = "cachMovement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<MovementDetail> movementDetails = new HashSet<>();

    @Column(length = 255)
    private String description;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_register_id", nullable = false)
    private CashRegister cashRegister;

    // public CashMovement(BigDecimal amount) {
    //     if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
    //         throw new InvalidArgumentExecption("O valor deve ser maior que zero.");
    //     }
    // }
    // public void setAmount(BigDecimal amount) {
    //     if (amount.compareTo(BigDecimal.ZERO) <= 0) {
    //         throw new InvalidArgumentExecption("O valor deve ser maior que zero.");
    //     }
    //     this.amount = amount;
    // }
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getTotalPayments() {
        return movementDetails.stream()
                .map(MovementDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addPaymentDetail(MovementDetail movementDetail) {
        if (movementDetail == null) {
            throw new IllegalArgumentException("MovementDetail não pode ser nulo.");
        }
        movementDetail.setCachMovement(this); // Configura a relação reversa
        this.movementDetails.add(movementDetail);
    }

    @Override
    public CashMovementDTO convertDTO() {
        return new CashMovementDTO(this);
    }

    @Override
    public CashMovementMinDTO convertMinDTO() {
        return new CashMovementMinDTO(this);
    }
}
