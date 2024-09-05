package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.CategoryDTO;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.ProductMinDTO;
import rccommerce.entities.Category;
import rccommerce.entities.Product;
import rccommerce.entities.Suplier;
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
	public Page<ProductMinDTO> findAll(String name, String reference, Pageable pageable) {

		Page<Product> result = repository.searchAll(name, reference, pageable);
		if (result.getContent().isEmpty()) {
			throw new ResourceNotFoundException("Produto não encontrado");
		}
		return result.map(x -> new ProductMinDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		Product result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
		return new ProductDTO(result);
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findByReference(String codBarra) {
		codBarra = String.format("0000000000000" + codBarra).substring(codBarra.length());
		Product result = repository.findByReference(codBarra)
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
				throw new DatabaseException("Nome informado já existe");
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
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Produto não encontrado");
		} catch (DataIntegrityViolationException e) {
			if (e.toString().contains("NAME NULLS FIRST")) {
				throw new DatabaseException("Nome informado já existe");
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
			throw new DatabaseException("Produto inserido em algum pedido, proibido exclusão");
		}
	}

	void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setUnit(dto.getUnit());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		entity.setReference(getReference(dto, entity));

		entity.getCategories().clear();
		for (CategoryDTO category : dto.getCategories()) {
			Category result = categoryRepository.findById(category.getId())
					.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
			entity.addCategory(result);
		}

		if (dto.getSuplier() == null) {
			entity.setSuplier(suplierRepository.findById(1L).get());
			return;
		}

		Suplier result = suplierRepository.findById(dto.getSuplier().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
		if (result == null) {
			throw new InvalidArgumentExecption("Fornecedor inexistente");
		}
		entity.setSuplier(result);
	}

	private String getReference(ProductDTO dto, Product entity) {
		String barCode = isValidBarCodeEAN(dto.getReference());

		if (entity.getId() != null && barCode.isEmpty()) {
			return generateEAN13(entity.getId());
		}

		if (barCode.isEmpty()) {
			return generateEAN13(repository.count() + 1L);
		}

		return barCode;
	}

	private String generateEAN13(Long id) {
		String codEan13 = id.toString();
		codEan13 = String.format("000000000000" + codEan13).substring(codEan13.length());
		int digit = checkDigit(codEan13);

		return codEan13 + Integer.toString(digit);
	}

	private String isValidBarCodeEAN(String barCode) {
		int digit;

		if (barCode.isEmpty()) {
			return "";
		}

		if (barCode.length() != 13) {
			throw new InvalidArgumentExecption("Código de barras inválido");
		}

		digit = checkDigit(barCode);
		if (Integer.parseInt(barCode.substring(12)) != digit) {
			throw new InvalidArgumentExecption("Código de barras inválido");
		}
		return barCode;
	}

	private int checkDigit(String codEan) {
		int digit;
		int sum = 0;
		int size = codEan.length();
		String checkSum = "131313131313";

		if (size == 13) {
			size--;
		}
		try {
			Long.parseLong(codEan);
		} catch (NumberFormatException e) {
			throw new InvalidArgumentExecption("Código de barras inválido");
		}

		for (int i = 0; i < size; i++) {
			sum += Character.getNumericValue(codEan.charAt(i)) * Character.getNumericValue(checkSum.charAt(i));
		}

		digit = 10 - (sum % 10);
		return digit == 10 ? 0 : digit;
	}
}
