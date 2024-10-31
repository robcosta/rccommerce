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

import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.entities.Operator;
import rccommerce.entities.Permission;
import rccommerce.entities.Role;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.repositories.OperatorRepository;
import rccommerce.repositories.PermissionRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.interfaces.GenericService;

@Service
public class OperatorService implements GenericService<Operator, OperatorDTO, OperatorMinDTO, Long> {

    @Autowired
    private OperatorRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<OperatorMinDTO> searchEntity(Long id, String name, String email, Pageable pageable) {
        return findBy(example(id, name, email), pageable);
    }

    @Override
    public JpaRepository<Operator, Long> getRepository() {
        return repository;
    }

    @Override
    public Operator createEntity() {
        return new Operator();
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getTranslatedEntityName() {
        // Pega a tradução do nome da entidade para "Client"
        return messageSource.getMessage("entity.Operator", null, Locale.getDefault());
    }

    @Override
    public void copyDtoToEntity(OperatorDTO dto, Operator entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setCommission(dto.getCommission());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(isValidPassword(dto.getPassword()));
        }

        entity.getPermissions().clear();
        if (dto.getPermissions().isEmpty()) {
            Permission result = permissionRepository.findByAuthority(PermissionAuthority.PERMISSION_NONE.getName());
            entity.addPermission(result);
        }

        for (String permission : dto.getPermissions()) {
            Permission result = permissionRepository.findByAuthority(permission);
            if (result == null) {
                throw new InvalidArgumentExecption("Permissão de acesso, inexistentes: " + permission);
            }
            entity.addPermission(result);
        }

        if (dto.getRoles().isEmpty()) {
            throw new InvalidArgumentExecption("Indicar pelo menos um nível de acesso");
        }
        entity.getRoles().clear();
        for (String authority : dto.getRoles()) {
            Role result = roleRepository.findByAuthority(authority);
            if (result == null) {
                throw new InvalidArgumentExecption("Nível 'ROLE' de acesso: " + authority + ", inexistentes");
            }
            entity.addRole(result);
        }
    }

    private Example<Operator> example(Long id, String name, String email) {
        Operator OperatorExample = createEntity();
        if (id != null) {
            OperatorExample.setId(id);
        }
        if (name != null && !name.isEmpty()) {
            OperatorExample.setNameUnaccented(name);
        }
        if (email != null && !email.isEmpty()) {
            OperatorExample.setEmail(email);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        return Example.of(OperatorExample, matcher);
    }
}
