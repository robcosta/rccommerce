package rccommerce.services;

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
import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.repositories.AuthRepository;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.IgnoredException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.Authentication;

@Service
public class ClientService implements GenericService<Client, ClientDTO, ClientMinDTO, Long> {

	@Autowired
	private ClientRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AuthRepository authRepository;

	@Autowired
	private Authentication authentication;
	
	public Page<ClientMinDTO> searchClient(@Valid ClientMinDTO dto, Pageable pageable) {
		authentication.authUser("READER", null);
		
		String name = dto.getName() != "" ? dto.getName():"";
		String cpf =  dto.getCpf() != "" ? dto.getCpf().replaceAll("[^0-9]", ""):"";
		String email =  dto.getEmail() != "" ? dto.getEmail():"";
		
		Page<Client> result = repository.searchAll(name, email, cpf, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Cliente não encontrado");
		}
		Page<ClientMinDTO> result2 =  result.map(x -> new ClientMinDTO(x));
		return result2;
	}

	@Override
	public void copyDtoToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		if (!dto.getPassword().isEmpty()) {
			entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		}
		entity.setCpf(dto.getCpf());
		entity.getRoles().clear();
		entity.addRole(roleRepository.findByAuthority("ROLE_CLIENT"));
		entity.addAuth(authRepository.findByAuth("NONE"));
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
	public RuntimeException messageException(RuntimeException e) {

		if (e.getClass().equals(NoSuchElementException.class)) {
			return new ResourceNotFoundException("Cliente não existe");
		}

		if (e.getClass().equals(DataIntegrityViolationException.class)) {
			if (e.toString().contains("EMAIL NULLS FIRST")) {
				return new DatabaseException("Email informado já existe");
			}
			if (e.toString().contains("CPF NULLS FIRST")) {
				return new DatabaseException("CPF informado já existe");
			}
			return new DatabaseException("Cliente possui pedidos em seu nome, exclusão proibida");
		}
		
		if (e.getClass().equals(EntityNotFoundException.class)) {
			return new ResourceNotFoundException("Cliente não encontrado");
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
