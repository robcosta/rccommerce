package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.ProductCategoryMinDTO;
import rccommerce.entities.ProductCategory;
import rccommerce.repositories.ProductCategoryRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;

@Profile("!disabled")
@Service
public class ProductCategoryService implements GenericService<ProductCategory, ProductCategoryDTO, ProductCategoryMinDTO, Long> {

    @Autowired
    private ProductCategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductCategoryMinDTO> searchEntity(Long id, String name, Pageable pageable) {
        Page<ProductCategory> result = repository.searchAll(
                id,
                AccentUtils.removeAccents(name),
                pageable);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma categoria de produto encontrada para estes critérios de busca.");
        }
        return result.map(ProductCategoryMinDTO::new);
    }

    @Override
    public JpaRepository<ProductCategory, Long> getRepository() {
        return repository;
    }

    @Override
    public ProductCategory createEntity() {
        return new ProductCategory();
    }
}
