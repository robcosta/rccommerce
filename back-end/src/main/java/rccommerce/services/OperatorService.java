package rccommerce.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Auth;
import rccommerce.entities.Operator;
import rccommerce.entities.Role;
import rccommerce.repositories.AuthRepository;
import rccommerce.repositories.OperatorRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.IgnoredException;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.Authentication;

@Service
public class OperatorService  implements GenericService<Operator, OperatorDTO, OperatorMinDTO, Long>{

	@Autowired
	private OperatorRepository repository;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AuthRepository authRepository;

	@Autowired
	private Authentication authentication;
	

	public Page<OperatorMinDTO> searchOperator(@Valid OperatorMinDTO dto, Pageable pageable) {
		authentication.authUser("READER", null);
		
		String name = dto.getName() != "" ? dto.getName():"";
		String email =  dto.getEmail() != "" ? dto.getEmail():"";
		
		Page<Operator> result = repository.searchAll(name, email, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Operador não encontrado");
		}
		//Page<OperatorMinDTO> result2 =  result.map(x -> new OperatorMinDTO(x));
		return result.map(x -> new OperatorMinDTO(x));
	}

	public void copyDtoToEntity(OperatorDTO dto, Operator entity) {
		UserMinDTO userLogged = userService.getMe();

		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setCommission(dto.getCommission());
		if (!dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}

		if (!userLogged.getRoles().containsAll(List.of("ROLE_ADMIN"))) {
			return;
		}

		entity.getAuths().clear();
		for (String auth : dto.getAuths()) {
			Auth result = authRepository.findByAuth(auth);
			if (result == null) {
				throw new InvalidArgumentExecption("Nível 'AUTH' de acesso inexistentes");
			}
			entity.addAuth(result);
		}

		if (dto.getRoles().isEmpty()) {
			throw new InvalidArgumentExecption("Indicar pelo menos um nível de acesso");
		}
		entity.getRoles().clear();
		for (String authority : dto.getRoles()) {
			Role result = roleRepository.findByAuthority(authority);
			if (result == null) {
				throw new InvalidArgumentExecption("Nível 'ROLE' de acesso inexistentes");
			}
			entity.addRole(result);
		}
	}

	@Override
	public JpaRepository<Operator, Long> getRepository() {
		return repository;
	}

	@Override
	public Operator createEntity() {
		return new Operator();
	}

	@Override
	public RuntimeException messageException(RuntimeException e) {

		if (e.getClass().equals(NoSuchElementException.class)) {
			return new ResourceNotFoundException("Operador não existe");
		}

		if (e.getClass().equals(DataIntegrityViolationException.class)) {
			if (e.toString().contains("EMAIL NULLS FIRST")) {
				return new DatabaseException("Email informado já existe");
			}
			return new DatabaseException("Operador com vínculos em outras tabelas, exclusão proibida");
		}
		
		if (e.getClass().equals(EntityNotFoundException.class)) {
			return new ResourceNotFoundException("Operador não encontrado");
		}

		return new IgnoredException("Erro ignorado");
	}
	

	@Override
	public void authentication(String auth, Long id) {
		if (auth.equalsIgnoreCase("INSERT")) {
			return;
		}
		authentication.authUser(auth, id);		
	}
}
