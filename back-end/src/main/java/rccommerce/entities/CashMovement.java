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
import jakarta.persistence.Index;
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
@Table(name = "tb_cash_movement", indexes = {
    @Index(name = "idx_cash_movement_cash_register_id", columnList = "cash_register_id")
})
public class CashMovement implements Convertible<CashMovementDTO, CashMovementMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CashMovementType cashMovementType;

    @OneToMany(mappedBy = "cashMovement", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private Set<MovementDetail> movementDetails = new HashSet<>();

    @Column(length = 255)
    private String description;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cash_register_id", nullable = false)
    private CashRegister cashRegister;

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getTotalAmount() {
        return movementDetails.stream()
                .map(MovementDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Set<MovementDetail> getMovementDetails() {
        return movementDetails;
    }

    public void addMovementDetail(MovementDetail movementDetail) {
        if (movementDetail == null) {
            throw new IllegalArgumentException("MovementDetail não pode ser nulo.");
        }
        // Relacionamento bidirecional
        movementDetail.setCashMovement(this);
        if (movementDetail.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            this.movementDetails.add(movementDetail);
        }
    }

    public void removeMovementDetail(MovementDetail movementDetail) {
        if (movementDetail != null) {
            this.movementDetails.remove(movementDetail); // Remove diretamente
        }
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
