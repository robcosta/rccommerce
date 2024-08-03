package rccommerce.dto;

import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.constraints.PositiveOrZero;
import rccommerce.entities.Operator;

public class OperatorDTO extends UserDTO{
	@PositiveOrZero(message = "Comissão deve ter um valor zero ou positivo")
	private Double commission;

	public OperatorDTO(Long id, String name, String email, String password, Double commission) {
		super(id, name, email, password);
		this.commission = commission;
	}

	public OperatorDTO(Operator entity) {
		super(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword());
		commission = entity.getCommission();
		for(GrantedAuthority role: entity.getRoles()){
			super.getRoles().add(role.getAuthority());
		}
	}

	public Double getCommission() {
		return commission;
	}
	
}
