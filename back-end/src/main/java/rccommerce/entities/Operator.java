package rccommerce.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "tb_operator")
public class Operator extends User {
	
	private Double commission;
	
	@OneToMany(mappedBy = "operator")
	private List<Order> orders = new ArrayList<>();

	public Operator() {
	}

	public Operator(Long id, String name, String email,String password, Double commission) {
		super(id, name, email, password);
		this.commission = commission;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}
	
	public List<Order> getOrders() {
		return orders;
	}
}
