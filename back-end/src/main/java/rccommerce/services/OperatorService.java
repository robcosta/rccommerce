package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.OperatorDTO;
import rccommerce.dto.OperatorMinDTO;
import rccommerce.entities.Operator;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.repositories.OperatorRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class OperatorService implements GenericService<Operator, OperatorDTO, OperatorMinDTO, Long> {

    @Autowired
    private OperatorRepository repository;

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
    public void checkUserPermissions(PermissionAuthority authority, Long id) {
        Long userId = SecurityContextUtil.getUserId(); // Obtém o ID do usuário autenticado

        //Verifica se o usuário é o próprio operador para permitir auto operações
        if (userId.equals(id)) {
            return;
        }

        // Para os demais casos, chama o método da interface GenericService
        GenericService.super.checkUserPermissions(authority);
    }
}
