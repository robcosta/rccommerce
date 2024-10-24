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
import rccommerce.entities.Permission;
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

		List<UserDetailsProjection> result = repository.searchUserRolesAndPermissionsByEmail(username);
		if (result.isEmpty()) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}

		User user = new User();
		user.setId(result.get(0).getUserId());
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
			user.addPermission(new Permission(projection.getPermissionId(), projection.getPermissionAuthority()));
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
			return repository.searchEmail(username).get();
		} catch (Exception e) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}
	}
}
