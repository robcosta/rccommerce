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

import rccommerce.dto.CategoryDTO;
import rccommerce.dto.CategoryMinDTO;
import rccommerce.entities.Category;
import rccommerce.repositories.CategoryRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.util.AccentUtils;

@Service
public class CategoryService implements GenericService<Category, CategoryDTO, CategoryMinDTO, Long> {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<CategoryMinDTO> searchEntity(Long id, String name, Pageable pageable) {
        return findBy(example(id, name), pageable);
    }

    @Override
    public JpaRepository<Category, Long> getRepository() {
        return repository;
    }

    @Override
    public Category createEntity() {
        return new Category();
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public void copyDtoToEntity(CategoryDTO dto, Category entity) {
        entity.setName(dto.getName());
    }

    private Example<Category> example(Long id, String name) {
        Category categoryExample = createEntity();
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
