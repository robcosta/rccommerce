package rccommerce.services.interfaces;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.SecurityContextUtil;

public interface GenericService<T extends Convertible<DTO, MINDTO>, DTO, MINDTO, ID> {

    JpaRepository<T, ID> getRepository();

    void copyDtoToEntity(DTO dto, T entity);

    T createEntity();

    String getTranslatedEntityName();

    @Transactional(readOnly = true)
    default Page<MINDTO> findAll(Pageable pageable) {
        checkUserPermissions(PermissionAuthority.PERMISSION_READER);

        Page<T> result = getRepository().findAll(pageable);
        if (result.getContent().isEmpty()) {
            handleResourceNotFound();
        }
        return result.map(x -> x.convertMinDTO());
    }

    @Transactional(readOnly = true)
    default MINDTO findById(ID id) {
        return findById(id, true);
    }

    @Transactional(readOnly = true)
    default MINDTO findById(ID id, boolean checkpermisson) {
        if (checkpermisson) {
            checkUserPermissions(PermissionAuthority.PERMISSION_READER, (Long) id);
        }

        T result = getRepository().findById(id).orElseThrow(() -> {
            handleResourceNotFound();
            return null;
        });

        return result.convertMinDTO();
    }

    @Transactional(readOnly = true)
    default Page<MINDTO> findBy(Example<T> example, Pageable pageable) {
        return findBy(example, true, pageable);
    }

    @Transactional(readOnly = true)
    default Page<MINDTO> findBy(Example<T> example, boolean checkpermisson, Pageable pageable) {
        if (checkpermisson) {
            checkUserPermissions(PermissionAuthority.PERMISSION_READER);
        }

        Page<T> result = getRepository().findBy(example, query -> query.page(pageable));

        if (result.getContent().isEmpty()) {
            handleResourceNotFound();
        }
        return result.map(x -> x.convertMinDTO());
    }

    @Transactional
    default MINDTO insert(DTO dto) {
        return insert(dto, true);
    }

    @Transactional
    default MINDTO insert(DTO dto, boolean checkpermisson) {
        if (checkpermisson) {
            checkUserPermissions(PermissionAuthority.PERMISSION_READER);
        }

        T entity = createEntity();
        copyDtoToEntity(dto, entity);
        try {
            entity = getRepository().save(entity);
            return entity.convertMinDTO();
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }
    }

    @Transactional
    default MINDTO update(DTO dto, ID id) {
        checkUserPermissions(PermissionAuthority.PERMISSION_UPDATE, (Long) id);

        try {
            T entity = getRepository().getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = getRepository().save(entity);
            return entity.convertMinDTO();
        } catch (EntityNotFoundException e) {
            handleResourceNotFound();
            return null;
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    default void delete(ID id) {
        checkUserPermissions(PermissionAuthority.PERMISSION_DELETE, (Long) id);

        if (!getRepository().existsById(id)) {
            handleResourceNotFound();
        }
        try {
            getRepository().deleteById(id);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
        }
    }

    // Métodos auxiliares para lançar a exceção
    default void handleDataIntegrityViolation(DataIntegrityViolationException e) {
        if (e.toString().contains("EMAIL NULLS FIRST")) {
            throw new DatabaseException("Email informado já existe");
        }
        if (e.toString().contains("NAME NULLS FIRST")) {
            throw new DatabaseException("Nome informado já existe");
        }
        if (e.toString().contains("CPF NULLS FIRST")) {
            throw new DatabaseException("CPF informado já existe");
        }
        if (e.toString().contains("CNPJ NULLS FIRST")) {
            throw new DatabaseException("CNPJ informado já existe");
        }

        throw new DatabaseException(getTranslatedEntityName() + " causando erro de integridade, operação proibida.");
    }

    default void handleResourceNotFound() {
        throw new ResourceNotFoundException(
                getTranslatedEntityName() + " não encontrado(a) para os critérios especificados.");
    }

    // Método auxiliar que verifica a permissão do usuário para acesso aos métodos do service.
    default void checkUserPermissions(PermissionAuthority authority, Long id) {
        checkUserPermissions(authority);
    }

    default void checkUserPermissions(PermissionAuthority authority) {
        List<String> authList = SecurityContextUtil.getAuthList(); // Obtém a lista de permissões do usuário

        // Permissão geral que concede todas as permissões (PERMISSION_ALL)
        if (authList.contains(PermissionAuthority.PERMISSION_ALL.getName())) {
            return; // Se o usuário tiver a permissão PERMISSION_ALL, permite qualquer operação
        }

        // Verifica se o usuário tem a permissão necessária para a operação atual
        // (CREATE, DELETE, etc.)
        if (authList.contains(authority.getName())) {
            return;
        }

        // Se não tiver permissões, lançar exceção
        throw new ForbiddenException("Usuário não tem permissão para acessar este recurso.");
    }
}
