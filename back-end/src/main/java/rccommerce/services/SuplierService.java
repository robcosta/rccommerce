package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.SuplierDTO;
import rccommerce.dto.SuplierMinDTO;
import rccommerce.entities.Suplier;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class SuplierService {

	@Autowired
	private SuplierRepository repository;



	@Transactional(readOnly = true)
	public Page<SuplierMinDTO> findAll(String name, String cnpj, Pageable pageable) {
	//	authentication.authUser("READER", null);
		
		Page<Suplier> result = repository.searchAll(name, cnpj, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Fornecedor não encontrado");
		}
		return result.map(x -> new SuplierMinDTO(x));
	}

	@Transactional(readOnly = true)
	public SuplierDTO findById(Long id) {
//		authentication.authUser("READER", null);
		
		Suplier result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
		return new SuplierDTO(result);
	}
	

	@Transactional
	public SuplierDTO insert(SuplierDTO dto) {
//		authentication.authUser("CREATE", null);

		try {
			Suplier entity = new Suplier();
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new SuplierDTO(entity);
		} catch (DataIntegrityViolationException e) {
				throw new DatabaseException("CNPJ informado já cadastrado");
		}
	}

	@Transactional
	public SuplierDTO update(SuplierDTO dto, Long id) {
//		authentication.authUser("UPDATE", id);
		
		try {
			Suplier entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new SuplierDTO(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Nome do fornecedor informado já existe");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
//		authentication.authUser("DELETE", id);

		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Fornecedor não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Fornecedor - Erro de integridade no banco de dados");
		}
	}

	void copyDtoToEntity(SuplierDTO dto, Suplier entity) {
		entity.setName(dto.getName());	
		entity.setCnpj(dto.getCnpj());
	}
}
