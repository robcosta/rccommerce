package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.entities.Operator;
import rccommerce.repositories.OperatorRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class OperatorService {

	@Autowired
	private OperatorRepository repository;
	
	@Autowired
	private UserService userService;

	@Transactional(readOnly = true)
	public Page<OperatorMinDTO> findAll(String name, Pageable pageable) {
		Page<Operator> result = repository.searchByName(name, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Operador não encontrado");
		}
		return result.map(x -> new OperatorMinDTO(x));
	}

	@Transactional(readOnly = true)
	public OperatorMinDTO findById(Long id) {
		Operator result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Operador não encontrado."));
		return new OperatorMinDTO(result);
	}

	@Transactional
	public OperatorMinDTO insert(OperatorDTO dto) {
		Operator entity = new Operator();
		copyDtoToEntity(dto, entity);
		try {
			entity = repository.save(entity);
			return new OperatorMinDTO(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Email informado já existe");
		}
	}

	@Transactional
	public OperatorMinDTO update(OperatorDTO dto, Long id) {
		try {
			Operator entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new OperatorMinDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Operador não encontrado");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Email informado já existe");			
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Operador não encontrado");
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
		
	private void copyDtoToEntity(OperatorDTO dto, Operator entity) {
		userService.copyDtoToEntity(dto, entity);
		entity.setCommission(dto.getCommission());
	}
}
