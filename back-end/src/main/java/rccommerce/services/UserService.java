package rccommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.RoleRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.InvalidPasswordExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.util.CustomUserUtil;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CustomUserUtil customUserUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}

		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}

		return user;
	}

	@Transactional(readOnly = true)
	public UserMinDTO getMe() {
		return new UserMinDTO(authenticated());
	}

	@Transactional(readOnly = true)
	public Page<UserMinDTO> findAll(String name, Pageable pageable) {
		Page<User> result = repository.searchByName(name, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Usuário não encontrado");
		}
		return result.map(x -> new UserMinDTO(x));
	}

	@Transactional(readOnly = true)
	public UserMinDTO findById(Long id) {
		User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
		return new UserMinDTO(user);
	}

	@Transactional
	public UserMinDTO insert(UserDTO dto) {
		checkPassword(dto.getPassword());
		User entity = new User();
		copyDtoToEntity(dto, entity);
		try {
			entity = repository.save(entity);
			return new UserMinDTO(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Email informado já existe");
		}
	}

	@Transactional
	public UserMinDTO update(UserDTO dto, Long id) {
		if (!dto.getPassword().isEmpty()) {
			checkPassword(dto.getPassword());
		}
		try {
			User entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new UserMinDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Usuário não encontrado");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Email informado já existe");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Usuário não encontrado");
		}
		if (id == authenticated().getId()) {
			throw new ForbiddenException("Proibida auto deleção");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	protected User authenticated() {
		try {
			String username = customUserUtil.getLoggerUsername();
			return repository.findByEmail(username).get();
		} catch (Exception e) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}
	}

	protected void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setCommission(dto.getCommission());
		if (!dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}
		entity.getRoles().clear();
		for (String authority : dto.getRoles()) {
			Role role = roleRepository.findByAuthority(authority);
			// role.setId(roleDTO.getId());
			entity.getRoles().add(role);
		}
	}

	protected boolean checkPassword(String password) {
		if (password.length() < 4) {
			throw new InvalidPasswordExecption("Senha tem de ter entre 4 e 8 caracteres.");
		}
		if(password.length() > 8) {
			throw new InvalidPasswordExecption("Senha tem de ter entre 4 e 8 caracteres.");
		}
		if (!password.matches("^\\d+$")) {
			throw new InvalidPasswordExecption("Senha deve conter apenas números.");
		}
		return true;
	}
}
