package rccommerce.services;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ser.std.StdArraySerializers.LongArraySerializer;

import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.dto.UserMinDTO;
import rccommerce.entities.Verify;
import rccommerce.entities.Operator;
import rccommerce.entities.Role;
import rccommerce.entities.enums.Very;
import rccommerce.repositories.VerifyRepository;
import rccommerce.repositories.OperatorRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.VerifyService;

@Service
public class OperatorService  implements GenericService<Operator, OperatorDTO, OperatorMinDTO, Long>{

	@Autowired
	private OperatorRepository repository;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private VerifyRepository VerifyRepository;

	@Autowired
	private VerifyService VerifyService;
	
   @Autowired
    private MessageSource messageSource;
	
	@Transactional(readOnly = true)
	public Page<OperatorMinDTO> searchEntity(Long id, String name, String email, Pageable pageable) {
		return searchAll(example(id, name, email), pageable);
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
	public void UserVerification(Very very, Long id) {
		VerifyService.veryUser(very, id);
	}
	
	@Override
	public String getTranslatedEntityName() {
		// Pega a tradução do nome da entidade para "Client"
        return messageSource.getMessage("entity.Operator", null, Locale.getDefault());
	}
	
	public void copyDtoToEntity(OperatorDTO dto, Operator entity) {
		UserMinDTO userLogged = userService.getMe();

		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail().toLowerCase());
		entity.setCommission(dto.getCommission());
		if (!dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}

		if (!userLogged.getRoles().containsAll(List.of("ROLE_ADMIN"))) {
			return;
		}

		entity.getVerified().clear();
		for (Very very : dto.getVery()) {
			Verify result = VerifyRepository.getReferenceById(very.getCode().longValue());
			if (result == null) {
				throw new InvalidArgumentExecption("Nível 'VERY' de acesso, inexistentes");
			}
			entity.addVerified(result);
		}

		if (dto.getRoles().isEmpty()) {
			throw new InvalidArgumentExecption("Indicar pelo menos um nível de acesso");
		}
		entity.getRoles().clear();
		for (String authority : dto.getRoles()) {
			Role result = roleRepository.findByAuthority(authority);
			if (result == null) {
				throw new InvalidArgumentExecption("Nível 'ROLE' de acesso, inexistentes");
			}
			entity.addRole(result);
		}
	}
	
	private Example<Operator> example(Long id, String name, String email) {
		Operator OperatorExample = createEntity();
		if (id != null) {
			OperatorExample.setId(id);
		}
		if (name != null && !name.isEmpty()) {
			OperatorExample.setNameUnaccented(name);
		}
		if (email != null && !email.isEmpty()) {
			OperatorExample.setEmail(email);
		}

		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())	
				.withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

		return Example.of(OperatorExample, matcher);
	}
}
