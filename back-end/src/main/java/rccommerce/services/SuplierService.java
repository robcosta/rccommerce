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

import rccommerce.dto.SuplierDTO;
import rccommerce.dto.SuplierMinDTO;
import rccommerce.entities.Suplier;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;

@Service
public class SuplierService implements GenericService<Suplier, SuplierDTO, SuplierMinDTO, Long> {

    @Autowired
    private SuplierRepository repository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<SuplierMinDTO> searchEntity(Long id, String name, String cnpj, Pageable pageable) {
        return findBy(example(id, name, cnpj), pageable);
    }

    @Override
    public void copyDtoToEntity(SuplierDTO dto, Suplier entity) {
        entity.setName(dto.getName());
        entity.setCnpj(dto.getCnpj());
    }

    @Override
    public JpaRepository<Suplier, Long> getRepository() {
        return repository;
    }

    @Override
    public Suplier createEntity() {
        return new Suplier();
    }

    @Override
    public String getTranslatedEntityName() {
        // Pega a tradução do nome da entidade para "Fornecedor" e aplicar nas mensagens de
        // erro"
        return messageSource.getMessage("entity.Suplier", null, Locale.getDefault());
    }

    private Example<Suplier> example(Long id, String name, String cnpj) {
        Suplier suplierExample = createEntity();
        if (id != null) {
            suplierExample.setId(id);
        }
        if (name != null && !name.isEmpty()) {
            suplierExample.setNameUnaccented(AccentUtils.removeAccents(name));
        }
        if (cnpj != null && !cnpj.isEmpty()) {
            suplierExample.setCnpj(cnpj);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.exact());

        return Example.of(suplierExample, matcher);
    }
}
