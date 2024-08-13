package rccommerce.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "tb_client")
public class Client extends User {
	
	@Column(unique = true)
	private String cpf;

	public Client() {
	}

	public Client(Long id, String name, String email,String password, String cpf) {
		super(id, name, email, password);
		this.cpf = cpf;
		super.roles.add(new Role(4L,"ROLE_CLIENT"));		
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf.replaceAll("[^0-9]", "");
	}
}
