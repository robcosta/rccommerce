package rccommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import rccommerce.dto.UserDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entity.Role;
import rccommerce.entity.User;
import rccommerce.projections.UserDetailsProjection;
import rccommerce.repository.RoleRepository;
import rccommerce.repository.UserRepository;
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
		if(result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		return result.map(x -> new UserMinDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserMinDTO findById(Long id) {
		User user = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado."));
		return new UserMinDTO(user);
	}
	
	@Transactional
	public @Valid UserMinDTO insert(@Valid UserDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new UserMinDTO(entity);
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
		if(dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}
		
		entity.getRoles().clear();
		
		for(String authority : dto.getRoles()) {
			Role role = roleRepository.findByAuthority(authority);
			//role.setId(roleDTO.getId());			
			entity.getRoles().add(role);
		}
	}

}
