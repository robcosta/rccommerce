package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.CategoryDTO;
import rccommerce.entities.Category;
import rccommerce.repositories.CategoryRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.Authentication;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Autowired
	private Authentication authentication;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAll(String name, Pageable pageable) {
		authentication.authUser("READER", null);
		
		Page<Category> result = repository.searchAll(name, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Categoria não encontrada");
		}
		return result.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		authentication.authUser("READER", null);
		
		Category result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
		return new CategoryDTO(result);
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findByName(String name) {
		authentication.authUser("READER", null);
		
		Category result = repository.findByName(name.toUpperCase())
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
		return new CategoryDTO(result);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		authentication.authUser("CREATE", null);

		try {
			Category entity = new Category();
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new CategoryDTO(entity);
		} catch (DataIntegrityViolationException e) {
				throw new DatabaseException("Nome da categoria informado já existe");
		}
	}

	@Transactional
	public CategoryDTO update(CategoryDTO dto, Long id) {
		authentication.authUser("UPDATE", id);
		
		try {
			Category entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new CategoryDTO(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Nome da categoria informado já existe");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		authentication.authUser("DELETE", id);

		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Categoria não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Categoria - Erro de integridade no banco de dados");
		}
	}

	void copyDtoToEntity(CategoryDTO dto, Category entity) {
		entity.setName(dto.getName());		
	}
}
