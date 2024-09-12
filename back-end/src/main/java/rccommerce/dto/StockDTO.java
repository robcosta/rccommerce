package rccommerce.dto;

import java.time.Instant;

import rccommerce.entities.Stock;
import rccommerce.entities.enums.StockMoviment;


public class StockDTO {
	
	private Long id;
	private UserMinDTO user;
	private Double quantity;
	private Double qttMoved;
	private Instant moment;
	private StockMoviment moviment;
	
	public StockDTO() {
	}

	public StockDTO(Long id, UserMinDTO user, Double quantity, Double qttMoved, Instant moment, StockMoviment moviment) {
		this.id = id;
		this.user = user;
		this.quantity = quantity;
		this.qttMoved = qttMoved;
		this.moment = moment;
		this.moviment = moviment;
	}

	public StockDTO(Stock entity) {
		id = entity.getId();
		user = new UserMinDTO(entity.getUser());
		quantity = entity.getQuantity();
		moment = entity.getMoment();
		qttMoved = entity.getQttMoved();
	}

	public Long getId() {
		return id;
	}

	public UserMinDTO getUser() {
		return user;
	}

	public Double getQuantity() {
		return quantity;
	}

	public Double getQttMoved() {
		return qttMoved;
	}

	public Instant getMoment() {
		return moment;
	}

	public StockMoviment getMoviment() {
		return moviment;
	}
}
