package rccommerce.entities;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import rccommerce.entities.enums.StockMoviment;


@Entity
@Table(name = "tb_stock")
public class Stock {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "User_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	private Double quantity;
	
	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
	private Instant moment;
	
	private Double qttMoved;
	
	private StockMoviment moviment;
	
	public Stock() {
	}

	public Stock(Long id, User user, Product product,  Double quantity, Instant moment, Double qttMoved) {
		this.id = id;
		this.user = user;
		this.product = product;
		this.quantity = quantity;
		this.moment = moment;
		this.qttMoved = qttMoved;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Instant getMoment() {
		return moment;
	}

	public void setMoment(Instant moment) {
		this.moment = moment;
	}

	public Double getQttMoved() {
		return qttMoved;
	}
	
	public StockMoviment getMoviment() {
		return moviment;
	}
	
	public void setMoviment(StockMoviment moviment) {
		this.moviment = moviment;
	}
	
	public void setQttMoved(Double qttMoved) {
		this.qttMoved = qttMoved;
		 switch (this.moviment) {  
	        case BUY:  
	        case INPUT:  
	            this.quantity += qttMoved;  
	            break;  
	        case SALE:  
	        case OUTPUT:  
	        case TRANSFER:  
	        	this.quantity -= qttMoved;
	            break;  
	        }  
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stock other = (Stock) obj;
		return Objects.equals(id, other.id);
	}
}
