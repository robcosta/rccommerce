package rccommerce.dto;

import java.time.Instant;

import rccommerce.entities.Payment;

public class PaymentMinDTO {

    private Long id;
    private Instant moment;
    private Long orderId;
    private String paymentType;

    public PaymentMinDTO(Long id, Instant moment, Long orderId, String paymentType) {
        this.id = id;
        this.moment = moment;
        this.orderId = orderId;
        this.paymentType = paymentType;
    }

    public PaymentMinDTO(Payment entity) {
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
