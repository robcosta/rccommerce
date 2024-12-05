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

    @Builder.Default
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PaymentDetail> paymentDetails = new HashSet<>();

    public void addPaymentDetail(PaymentDetail paymentDetail) {
        if (paymentDetail == null) {
            throw new IllegalArgumentException("PaymentDetail não pode ser nulo.");
        }
        // Configurar a relação bidirecional
        paymentDetail.setPayment(this);
        this.paymentDetails.add(paymentDetail);
    }

    public void removePaymentDetail(PaymentDetail paymentDetail) {
        if (paymentDetail != null && this.paymentDetails.remove(paymentDetail)) {
            paymentDetail.setPayment(null); // Remove a referência reversa
        }
    }

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getTotalPayments() {
        return paymentDetails.stream()
                .map(PaymentDetail::getAmount)
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
