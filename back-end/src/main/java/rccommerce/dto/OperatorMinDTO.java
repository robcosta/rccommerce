package rccommerce.dto;

import rccommerce.entities.Operator;

public class OperatorMinDTO extends UserMinDTO {

	private Double commission;

	public OperatorMinDTO(Long id, String name, String email, Double commission) {
		super(id, name, email);
		this.commission = commission;
	}

	public OperatorMinDTO(Operator entity) {
		super(entity);
		commission = entity.getCommission();
	}

	public Double getCommission() {
		return commission;
	}
	
}
