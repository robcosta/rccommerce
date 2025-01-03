package rccommerce.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.ProductMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.entities.ProductStock;
import rccommerce.entities.Suplier;
import rccommerce.repositories.CategoryRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;

@Service
public class ProductService implements GenericService<Product, ProductDTO, ProductMinDTO, Long> {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SuplierRepository suplierRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> searchEntity(Long id, String name, String reference, Pageable pageable) {
        return findBy(example(id, name, reference), false, pageable);
    }

    @Transactional(readOnly = true)
    public ProductDTO findByReference(String codBarra) {
        codBarra = String.format("0000000000000" + codBarra).substring(codBarra.length());

        Product result = repository.findByReference(codBarra)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
        return new ProductDTO(result);
    }

    @Transactional
    public void updateStock(List<ProductStock> stocks) {
        for (ProductStock productStock : stocks) {
            try {
                Product entity = repository.getReferenceById(productStock.getProduct().getId());
                entity.setQuantity(productStock.getQuantity());
                repository.save(entity);
            } catch (EntityNotFoundException e) {
                throw new ResourceNotFoundException("Produto não encontrado");
            }
        }
        stockRepository.saveAll(stocks);
    }

    @Override
    public void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setUnit(dto.getUnit());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setQuantity(new BigDecimal(0.00));
        entity.setReference(getReference(dto, entity));

        entity.getCategories().clear();
        for (ProductCategoryDTO category : dto.getCategories()) {
            ProductCategory result = categoryRepository.findById(category.getId())
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
            Long.valueOf(codEan);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentExecption("Código de barras inválido");
        }

        for (int i = 0; i < size; i++) {
            sum += Character.getNumericValue(codEan.charAt(i)) * Character.getNumericValue(checkSum.charAt(i));
        }

        digit = 10 - (sum % 10);
        return digit == 10 ? 0 : digit;
    }

    @Override
    public JpaRepository<Product, Long> getRepository() {
        return repository;
    }

    @Override
    public Product createEntity() {
        return new Product();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.Product", null, Locale.getDefault());
    }

    private Example<Product> example(Long id, String name, String reference) {
        Product productExample = createEntity();
        if (id != null) {
            productExample.setId(id);
        }
        if (name != null && !name.isEmpty()) {
            productExample.setNameUnaccented(AccentUtils.removeAccents(name));
        }
        if (reference != null && !reference.isEmpty()) {
            productExample.setReference(AccentUtils.removeAccents(reference));
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("reference", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        return Example.of(productExample, matcher);
    }
}
