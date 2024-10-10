package rccommerce.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import rccommerce.entities.enums.Very;

@Entity
@Table(name = "tb_verify")
public class Verify {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(unique = true)
	private Very very;

	public Verify() {
	}

	public Verify(Long id, Very very) {
		this.id = id;
		this.very = very;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Very getVery() {
		return very;
	}

	public void addVery(Very very) {
		this.very = very;
	}

	@Override
	public int hashCode() {
		return Objects.hash(very);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Verify other = (Verify) obj;
		return very == other.very;
	}
}
