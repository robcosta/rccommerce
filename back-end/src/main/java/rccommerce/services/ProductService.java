package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.ProductDTO;
import rccommerce.entities.Category;
import rccommerce.entities.Product;
import rccommerce.repositories.CategoryRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.Authentication;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private SuplierRepository suplierRepository;

	@Autowired
	private Authentication authentication;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(String name, String reference, Pageable pageable) {

		Page<Product> result = repository.searchAll(name, reference, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Produto não encontrado");
		}
		return result.map(x -> new ProductDTO(x));
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		Product result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
		return new ProductDTO(result);
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		authentication.authUser("CREATE", null);

		try {
			Product entity = new Product();
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new ProductDTO(entity);
		} catch (DataIntegrityViolationException e) {
			if (e.toString().contains("NAME NULLS FIRST")) {
				throw new DatabaseException("name informado já existe");
			}
			throw new DatabaseException("Código de barras já cadastrado");
		}
	}

	@Transactional
	public ProductDTO update(ProductDTO dto, Long id) {
		authentication.authUser("UPDATE", id);
		try {
			Product entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.saveAndFlush(entity);
			return new ProductDTO(entity);
		} catch (DataIntegrityViolationException e) {
			if (e.toString().contains("NAME NULLS FIRST")) {
				throw new DatabaseException("name informado já existe");
			}
			throw new DatabaseException("Código de barras já cadastrado");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		authentication.authUser("DELETE", id);

		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Produto não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setUnit(dto.getUnit());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		entity.setReference(dto.getReference());
		
		entity.getCategories().clear();
		for (String category : dto.getCategories()) {
			Category result = categoryRepository.findByCategory(category);
			if (result == null) {
				throw new InvalidArgumentExecption("Categoria inexistente");
			}
			entity.addCategory(result);
		}

		if (!dto.getSuplier().isEmpty()) {
			entity.setSuplier(suplierRepository.findBySuplier(dto.getSuplier()));
		}
		entity.setSuplier(suplierRepository.findById(1L).get());
	}
}
