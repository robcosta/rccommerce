package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.repositories.ClientRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;

	@Autowired
	private UserService userService;

	@Transactional(readOnly = true)
	public Page<ClientMinDTO> findAll(String name, Pageable pageable) {
		Page<Client> result = repository.searchByName(name, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Cliente não encontrado");
		}
		return result.map(x -> new ClientMinDTO(x));
	}

	@Transactional(readOnly = true)
	public ClientMinDTO findById(Long id) {
		Client result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
		return new ClientMinDTO(result);
	}

	@Transactional
	public ClientMinDTO insert(ClientDTO dto) {
		userService.checkPassword(dto.getPassword());
		Client entity = new Client();
		copyDtoToEntity(dto, entity);
		try {
			entity = repository.saveAndFlush(entity);
			return new ClientMinDTO(entity);
		} catch (DataIntegrityViolationException e) {
			if (e.toString().contains("EMAIL NULLS FIRST")) {
				throw new DatabaseException("Email informado já existe");
			}
			throw new DatabaseException("CPF informado já existe");
		}
	}

	@Transactional
	public ClientMinDTO update(ClientDTO dto, Long id) {
		if (!dto.getPassword().isEmpty()) {
			userService.checkPassword(dto.getPassword());
		}
		try {
			Client entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new ClientMinDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Cliente não encontrado");
		} catch (DataIntegrityViolationException e) {
			if(e.toString().contains("EMAIL NULLS FIRST")) {
				throw new DatabaseException("Email informado já existe");
			}
			throw new DatabaseException("CPF informado já existe");			
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Cliente não encontrado");
		}
		if (id == userService.authenticated().getId()) {
			throw new ForbiddenException("Proibida auto deleção");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	private void copyDtoToEntity(ClientDTO dto, Client entity) {
		userService.copyDtoToEntity(dto, entity);
		entity.setCpf(dto.getCpf());
	}
}
