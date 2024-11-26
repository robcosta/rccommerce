package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
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
import rccommerce.entities.enums.PaymentType;
import rccommerce.services.interfaces.Convertible;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "tb_payment")
public class Payment implements Convertible<PaymentDTO, PaymentMinDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;

    private PaymentType paymentType;

    private BigDecimal amount;

    @OneToOne
    @MapsId
    private Order order;

    @Override
    public PaymentDTO convertDTO() {
        return new PaymentDTO(this);
    }

    @Override
    public PaymentMinDTO convertMinDTO() {
        return new PaymentMinDTO(this);
    }
}
