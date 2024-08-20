package rccommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.Authentication;

@Service
public class OperatorService {

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

	@Transactional(readOnly = true)
	public Page<OperatorMinDTO> findAll(String name, String email, Pageable pageable) {
		authentication.authUser("READER", null);

		Page<Operator> result = repository.searchAll(name, email, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Operador não encontrado");
		}
		return result.map(x -> new OperatorMinDTO(x));
	}

	@Transactional(readOnly = true)
	public OperatorMinDTO findById(Long id) {
		authentication.authUser("READER", id);

		Operator result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Operador não encontrado."));
		return new OperatorMinDTO(result);
	}

	@Transactional
	public OperatorMinDTO insert(OperatorDTO dto) {
		authentication.authUser("CREATE", null);

		try {
			Operator entity = new Operator();
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new OperatorMinDTO(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Email informado já existe");
		}
	}

	@Transactional
	public OperatorMinDTO update(OperatorDTO dto, Long id) {
		if (id == 1L) {
			throw new ForbiddenException("ADMINSTRADOR MASTER - Atualização proibida");
		}
		authentication.authUser("UPDATE", id);
		try {
			Operator entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new OperatorMinDTO(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Email informado já existe");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (id == 1L) {
			throw new ForbiddenException("ADMINSTRADOR MASTER - Deleção proibida");
		}
		authentication.authUser("DELETE", id);

		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Operador não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	void copyDtoToEntity(OperatorDTO dto, Operator entity) {
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
}
