package rccommerce.services.interfaces;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.util.Convertible;


public interface GenericService<T extends Convertible<DTO,MINDTO>, DTO, MINDTO, ID> {
	
	JpaRepository<T, ID> getRepository();
	
	void copyDtoToEntity(DTO dto, T entity);
	
	T createEntity();
	
	RuntimeException messageException(RuntimeException e);
	
	void authentication(String auth, ID id);

	@Transactional(readOnly = true)
	default Page<MINDTO> findAll(Pageable pageable) {
		authentication("READER", null);
		
		Page<T> result =  getRepository().findAll(pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Cliente não encontrado");
		}
		return result.map(x -> x.convertMinDTO());
	}

	@Transactional(readOnly = true)
	default MINDTO findById(ID id) {
		authentication("READER", id);
		
		try {
			Optional<T> result = getRepository().findById(id);			
			return result.get().convertMinDTO();
		} catch (RuntimeException e) {
			throw messageException(e);
		}
	}

	@Transactional
	default MINDTO insert(DTO dto) {
		authentication("INSERT", null);
		
		T entity = createEntity();
		copyDtoToEntity(dto, entity);	
		try {
			entity = getRepository().saveAndFlush(entity);
			return entity.convertMinDTO();
		} catch (RuntimeException e) {
			throw messageException(e);
		}
	}

	@Transactional
	default MINDTO update(DTO dto, ID id) {
		authentication("UPDATE", id);
		
		try {
			T entity = getRepository().getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = getRepository().saveAndFlush(entity);
			return entity.convertMinDTO();
		} catch (RuntimeException e) {
			throw messageException(e);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	default void delete(ID id) {
		authentication("DELETE", id);
		
		if (!getRepository().existsById(id)) {
			throw messageException(new EntityNotFoundException());
		}
		try {
			getRepository().deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw messageException(e);
		}
	}
}
