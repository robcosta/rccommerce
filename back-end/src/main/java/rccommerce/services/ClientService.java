package rccommerce.services;

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
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.enums.Very;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.repositories.VerifyRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.VerifyService;
import rccommerce.util.AccentUtils;

@Service
public class ClientService implements GenericService<Client, ClientDTO, ClientMinDTO, Long> {

	@Autowired
	private ClientRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private VerifyRepository verifyRepository;

	@Autowired
	private VerifyService verifyService;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public Page<ClientMinDTO> searchEntity(Long id, String name, String email, String cpf, Pageable pageable) {
		return searchAll(example(id, name, email, cpf), pageable);
	}

	@Override
	public JpaRepository<Client, Long> getRepository() {
		return repository;
	}

	@Override
	public Client createEntity() {
		return new Client();
	}

	@Override
	public void UserVerification(Very very, Long id) {
		if (very.equals(Very.CREATE)) {
			return;
		}
		verifyService.veryUser(very, id);
	}
	
	@Override
	public void copyDtoToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail().toLowerCase());
		if (!dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}
		entity.setCpf(dto.getCpf());
		entity.getRoles().clear();
		entity.addRole(roleRepository.findByAuthority("ROLE_CLIENT"));
		entity.getVerified().clear();
		entity.addVerified(verifyRepository.getReferenceById(Very.NONE.getCode().longValue()));
	}

	@Override
	public String getTranslatedEntityName() {
		// Pega a tradução do nome da entidade para "Client" e aplicar nas mensagens de erro"
		return messageSource.getMessage("entity.Client", null, Locale.getDefault());
	}

	private Example<Client> example(Long id,String name, String email, String cpf) {
		Client clientExample = createEntity();
		if (id != null) {
			clientExample.setId(id);
		}
		if (name != null && !name.isEmpty()) {
			clientExample.setNameUnaccented(AccentUtils.removeAccents(name));
		}
		if (email != null && !email.isEmpty()) {
			clientExample.setEmail(AccentUtils.removeAccents(email));
		}
		if (cpf != null && !cpf.isEmpty()) {
			clientExample.setCpf(cpf);
		}

		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())	
				.withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.exact());

		return Example.of(clientExample, matcher);
	}
}
