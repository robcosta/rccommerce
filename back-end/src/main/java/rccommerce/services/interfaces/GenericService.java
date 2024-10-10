package rccommerce.services.interfaces;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.entities.enums.Very;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;

public interface GenericService<T extends Convertible<DTO, MINDTO>, DTO, MINDTO, ID> {
	
	JpaRepository<T, ID> getRepository();

	void copyDtoToEntity(DTO dto, T entity);

	T createEntity();

	void UserVerification(Very very, ID id);
	
	String getTranslatedEntityName();

	@Transactional(readOnly = true)
	default Page<MINDTO> findAll(Pageable pageable) {
		UserVerification(Very.READER, null);

		Page<T> result = getRepository().findAll(pageable);
		if (result.getContent().isEmpty()) {
			throwResourceNotFound();	
		}
		return result.map(x -> x.convertMinDTO());
	}

	@Transactional(readOnly = true)
	default Page<MINDTO> searchAll(Example<T> example, Pageable pageable) {
		UserVerification(Very.READER, null);

		Page<T> result = getRepository().findAll(example, pageable);
		if (result.isEmpty()) {
			throwResourceNotFound();			
		}
		return result.map(x -> x.convertMinDTO());
	}

	@Transactional(readOnly = true)
	default MINDTO findById(ID id) {
		UserVerification(Very.READER, id);
		
		T result = getRepository().findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(getTranslatedEntityName() + " não encontrado para os critérios especificados."));
		
		return result.convertMinDTO();
	}
	
	@Transactional(readOnly = true)
	default Page<MINDTO> findBy(Example<T> example, Pageable pageable) {
		UserVerification(Very.READER, null);
		
			Page<T> result = getRepository().findBy(example, query -> query.page(pageable));
			
			if (result.getContent().isEmpty()) {
				throwResourceNotFound();	
			}
			return result.map(x -> x.convertMinDTO()); 
	}

	@Transactional
	default MINDTO insert(DTO dto) {
		UserVerification(Very.CREATE, null);

		T entity = createEntity();
		copyDtoToEntity(dto, entity);
		try {
			entity = getRepository().saveAndFlush(entity);
			return entity.convertMinDTO();
		} catch (DataIntegrityViolationException e) {
			handleDataIntegrityViolation(e);
			return null;
		}
	}

	@Transactional
	default MINDTO update(DTO dto, ID id) {
		UserVerification(Very.UPDATE, id);

		try {
			T entity = getRepository().getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = getRepository().saveAndFlush(entity);
			return entity.convertMinDTO();
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(getTranslatedEntityName() + " não encontrado para os critérios especificados.");
		} catch (DataIntegrityViolationException e) {
			handleDataIntegrityViolation(e);
			return null;
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	default void delete(ID id) {
		UserVerification(Very.DELETE, id);

		if (!getRepository().existsById(id)) {
			throwResourceNotFound();
		}
		try {
			getRepository().deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throwResourceNotFound();
		}
	}
	
	default void handleDataIntegrityViolation(DataIntegrityViolationException e) {
	    if (e.toString().contains("EMAIL NULLS FIRST")) {
	        throw new DatabaseException("Email informado já existe");
	    }
	    if (e.toString().contains("NAME NULLS FIRST")) {
	        throw new DatabaseException("Nome informado já existe");
	    }
	    if (e.toString().contains("CPF NULLS FIRST")) {
	        throw new DatabaseException("CPF informado já existe");
	    }
	    throw new DatabaseException(getTranslatedEntityName() + "com vínculos em outras tabelas, exclusão proibida");	
	}
	
	// Método auxiliar para lançar a exceção
	default void throwResourceNotFound() {
		throw new ResourceNotFoundException(getTranslatedEntityName() + " não encontrado para os critérios especificados.");
	}
}
