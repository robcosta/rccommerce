package rccommerce.dto;

import org.springframework.security.core.GrantedAuthority;

import rccommerce.entities.Operator;

public class OperatorMinDTO extends UserMinDTO {

	private Double commission;

	public OperatorMinDTO(Long id, String name, String email, Double commission) {
		super(id, name, email);
		this.commission = commission;
	}

	public OperatorMinDTO(Operator entity) {
		super(entity.getId(), entity.getName(), entity.getEmail());
		commission = entity.getCommission();
		for(GrantedAuthority role: entity.getRoles()){
			super.getRoles().add(role.getAuthority());
		}
	}

	public Double getCommission() {
		return commission;
	}
	
}
