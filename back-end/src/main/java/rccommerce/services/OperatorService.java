package rccommerce.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;
import rccommerce.services.util.SecurityContextUtil;
import rccommerce.services.util.ValidPassword;

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
        Page<Operator> result = repository.searchAll(
                id,
                AccentUtils.removeAccents(name),
                AccentUtils.removeAccents(email),
                pageable);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum operador encontrado");
        }
        return result.map(OperatorMinDTO::new);
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
    public String getTranslatedEntityName() {
        // Pega a tradução do nome da entidade para "Client"
        return messageSource.getMessage("entity.Operator", null, Locale.getDefault());
    }

    @Override
    public void checkUserPermissions(PermissionAuthority authority, Long id) {
        Long userId = SecurityContextUtil.getUserId(); // Obtém o ID do usuário autenticado

        //Verifica se o usuário é o próprio operador para permitir auto operações
        if (userId.equals(id)) {
            return;
        }

        // Para os demais casos, chama o método da interface GenericService
        GenericService.super.checkUserPermissions(authority);
    }

    @Override
    public void copyDtoToEntity(OperatorDTO dto, Operator entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setCommission(dto.getCommission());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(ValidPassword.isValidPassword(dto.getPassword()));
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
}
