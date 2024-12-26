package rccommerce.services;

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

import rccommerce.dto.ProductCategoryDTO;
import rccommerce.dto.ProductCategoryMinDTO;
import rccommerce.entities.ProductCategory;
import rccommerce.repositories.CategoryRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;

@Service
public class CategoryService implements GenericService<ProductCategory, ProductCategoryDTO, ProductCategoryMinDTO, Long> {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<ProductCategoryMinDTO> searchEntity(Long id, String name, Pageable pageable) {
        return findBy(example(id, name), pageable);
    }

    @Override
    public JpaRepository<ProductCategory, Long> getRepository() {
        return repository;
    }

    @Override
    public ProductCategory createEntity() {
        return new ProductCategory();
    }

    @Override
    public void copyDtoToEntity(ProductCategoryDTO dto, ProductCategory entity) {
        entity.setName(dto.getName());
    }

    private Example<ProductCategory> example(Long id, String name) {
        ProductCategory categoryExample = createEntity();
        if (id != null) {
            categoryExample.setId(id);
        }
        if (name != null && !name.isEmpty()) {
            categoryExample.setNameUnaccented(AccentUtils.removeAccents(name));
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        return Example.of(categoryExample, matcher);
    }

    @Override
    public String getTranslatedEntityName() {
        // Pega a tradução do nome da entidade para "Category"
        return messageSource.getMessage("entity.Category", null, Locale.getDefault());
    }

}
