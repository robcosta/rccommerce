package rccommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.util.CustomUserUtil;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

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
	public Page<UserMinDTO> findAll(String name, String email, Pageable pageable) {
		Page<User> result = repository.searchAll(name, email, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Usuário não encontrado");
		}
		return result.map(x -> new UserMinDTO(x));
	}

	@Transactional(readOnly = true)
	public UserMinDTO findById(Long id) {
		User result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
		return new UserMinDTO(result);
	}

	public UserMinDTO findByEmail(String email) {
		User result = repository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
		return new UserMinDTO(result);
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
}
