package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.PaymentDTO;
import rccommerce.dto.PaymentMinDTO;
import rccommerce.services.interfaces.Convertible;
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_payment")
public class Payment implements Convertible<PaymentDTO, PaymentMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    private Long id;

    @OneToOne
    @MapsId
    private Order order;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;

    // @OneToMany(mappedBy = "payment")//, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    // private Set<MovementDetail> movementDetails = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<MovementDetail> movementDetails = new HashSet<>();

    // public void addPaymentDetail(MovementDetail movementDetail) {
    //     if (movementDetail == null) {
    //         throw new IllegalArgumentException("MovementDetail não pode ser nulo.");
    //     }
    //     // Configurar a relação bidirecional
    //     movementDetail.setPayment(this);
    //     this.movementDetails.add(movementDetail);
    // }
    public void addPaymentDetail(MovementDetail movementDetail) {
        if (movementDetail == null) {
            throw new IllegalArgumentException("MovementDetail não pode ser nulo.");
        }
        this.movementDetails.add(movementDetail); // Adiciona sem configurar relação reversa
    }

    public void removePaymentDetail(MovementDetail movementDetail) {
        if (movementDetail != null && this.movementDetails.remove(movementDetail)) {
            movementDetail.setPayment(null); // Remove a referência reversa
        }
    }

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getTotalPayments() {
        return movementDetails.stream()
                .map(MovementDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PaymentDTO convertDTO() {
        return new PaymentDTO(this);
    }

    @Override
    public PaymentMinDTO convertMinDTO() {
        return new PaymentMinDTO(this);
    }
}
