package rccommerce.dto;

import rccommerce.entities.Operator;

public class OperatorOrderDTO {
	
	private Long id;
	private String name;
	private Double commission;
	
	public OperatorOrderDTO(Long id, String name, Double commission) {
		this.id = id;
		this.name = name;
		this.commission = commission;
	}
	
	public OperatorOrderDTO(Operator entity) {
		id = entity.getId();
		name = entity.getName();
		commission = entity.getCommission();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Double getCommission() {
		return commission;
	}
}
