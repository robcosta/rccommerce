package rccommerce.dto;

import java.time.Instant;

import rccommerce.entities.Payment;

public class PaymentDTO {

    private Long id;
    private Instant moment;
    private Long orderId;
    private String paymentType;

    public PaymentDTO(Long id, Instant moment, Long orderId, String paymentType) {
        this.id = id;
        this.moment = moment;
        this.orderId = orderId;
        this.paymentType = paymentType;
    }

    public PaymentDTO(Payment entity) {
        id = entity.getId();
        moment = entity.getMoment();
        paymentType = entity.getPaymentType().getName();
        orderId = entity.getOrder().getId();
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Long getOrderId() {
        return orderId;
    }
}
