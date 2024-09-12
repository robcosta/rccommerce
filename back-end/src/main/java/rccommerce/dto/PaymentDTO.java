package rccommerce.dto;

import java.time.Instant;

import rccommerce.entities.Payment;
import rccommerce.entities.enums.PaymentType;

public class PaymentDTO {

	private Long id;
	private Instant moment;
	private Long orderId;
	private PaymentType paymentType;

	public PaymentDTO(Long id, Instant moment, Long orderId, PaymentType paymentType) {
		this.id = id;
		this.moment = moment;
		this.orderId = orderId;
		this.paymentType = paymentType;
	}

	public PaymentDTO(Payment entity) {
		id = entity.getId();
		moment = entity.getMoment();
		paymentType = entity.getPaymentType();
		orderId = entity.getOrder().getId();
	}

	public Long getId() {
		return id;
	}

	public Instant getMoment() {
		return moment;
	}
	
	public PaymentType getPaymentType() {
		return paymentType;
	}
	
	public Long getOrderId() {
		return orderId;
	}
}
