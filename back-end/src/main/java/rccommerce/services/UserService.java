package rccommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.RoleRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
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
		if (!dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}
		entity.getRoles().clear();
		for (String authority : dto.getRoles()) {
			Role role = roleRepository.findByAuthority(authority);
			if(role == null) {
				throw new InvalidArgumentExecption("roles inexistente");
			}
			entity.getRoles().add(role);
		}
	}

	protected boolean checkPassword(String password) {
		if (password.length() < 4) {
			throw new InvalidArgumentExecption("Senha tem de ter entre 4 e 8 caracteres.");
		}
		if(password.length() > 8) {
			throw new InvalidArgumentExecption("Senha tem de ter entre 4 e 8 caracteres.");
		}
		if (!password.matches("^\\d+$")) {
			throw new InvalidArgumentExecption("Senha deve conter apenas números.");
		}
		return true;
	}
}
