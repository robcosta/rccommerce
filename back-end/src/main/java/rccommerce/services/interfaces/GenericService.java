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
import rccommerce.entities.interfaces.TranslatableEntity;
import rccommerce.services.exceptions.DatabaseException;
import rccommerce.services.exceptions.ForbiddenException;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.SecurityContextUtil;

/**
 * Interface genérica que fornece operações CRUD básicas e controle de
 * permissões.
 *
 * @param <ENTITY> Tipo da entidade que deve implementar Convertible e
 * TranslatableEntity
 * @param <DTO> Tipo do DTO completo da entidade
 * @param <MINDTO> Tipo do DTO resumido da entidade
 * @param <ID> Tipo do identificador da entidade
 */
public interface GenericService<ENTITY extends Convertible2<ENTITY, DTO, MINDTO> & TranslatableEntity, DTO, MINDTO, ID> {

    /**
     * @return Repositório JPA associado à entidade
     */
    JpaRepository<ENTITY, ID> getRepository();

    /**
     * @return Nova instância da entidade
     */
    ENTITY createEntity();

    /**
     * Busca paginada de todas as entidades. Requer permissão PERMISSION_READER.
     *
     * @param pageable configuração de paginação
     * @return página contendo DTOs resumidos das entidades
     * @throws ForbiddenException se usuário não tem permissão
     * @throws ResourceNotFoundException se nenhum registro for encontrado
     */
    @Transactional(readOnly = true)
    default Page<MINDTO> findAll(Pageable pageable) {
        checkUserPermissions(PermissionAuthority.PERMISSION_READER);

        Page<ENTITY> result = getRepository().findAll(pageable);
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

        ENTITY result = getRepository().findById(id).orElseThrow(() -> {
            handleResourceNotFound();
            return null;
        });

        return result.convertMinDTO();
    }

    @Transactional(readOnly = true)
    default Page<MINDTO> findBy(Example<ENTITY> example, Pageable pageable) {
        return findBy(example, true, pageable);
    }

    @Transactional(readOnly = true)
    default Page<MINDTO> findBy(Example<ENTITY> example, boolean checkpermisson, Pageable pageable) {
        if (checkpermisson) {
            checkUserPermissions(PermissionAuthority.PERMISSION_READER);
        }

        Page<ENTITY> result = getRepository().findBy(example, query -> query.page(pageable));

        if (result.getContent().isEmpty()) {
            handleResourceNotFound();
        }
        return result.map(x -> x.convertMinDTO());
    }

    /**
     * Insere uma nova entidade. Requer permissão PERMISSION_CREATE.
     *
     * @param dto dados da entidade a ser criada
     * @return DTO resumido da entidade criada
     * @throws ForbiddenException se usuário não tem permissão
     * @throws DatabaseException se houver violação de integridade
     */
    @Transactional
    default MINDTO insert(DTO dto) {
        return insert(dto, true);
    }

    @Transactional
    default MINDTO insert(DTO dto, boolean checkpermisson) {
        if (checkpermisson) {
            checkUserPermissions(PermissionAuthority.PERMISSION_CREATE);
        }

        ENTITY entity = createEntity();

        try {
            entity = getRepository().save(entity.convertEntity(dto));
            return entity.convertMinDTO();
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }
    }

    /**
     * Atualiza uma entidade existente. Requer permissão PERMISSION_UPDATE.
     *
     * @param dto dados atualizados da entidade
     * @param id identificador da entidade
     * @return DTO resumido da entidade atualizada
     * @throws ForbiddenException se usuário não tem permissão
     * @throws ResourceNotFoundException se entidade não for encontrada
     * @throws DatabaseException se houver violação de integridade
     */
    @Transactional
    default MINDTO update(DTO dto, ID id) {
        return update(dto, id, true);
    }

    @Transactional
    default MINDTO update(DTO dto, ID id, boolean checkpermisson) {
        if (checkpermisson) {
            checkUserPermissions(PermissionAuthority.PERMISSION_UPDATE, (Long) id);
        }

        try {
            ENTITY entity = getRepository().getReferenceById(id);
            entity = getRepository().saveAndFlush(entity.convertEntity(dto));
            return entity.convertMinDTO();
        } catch (EntityNotFoundException e) {
            handleResourceNotFound();
            return null;
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }
    }

    /**
     * Remove uma entidade. Requer permissão PERMISSION_DELETE.
     *
     * @param id identificador da entidade
     * @throws ForbiddenException se usuário não tem permissão
     * @throws ResourceNotFoundException se entidade não for encontrada
     * @throws DatabaseException se houver dependências que impeçam a exclusão
     */
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
        ENTITY entity = createEntity();
        String errorMessage = e.getMessage().toUpperCase();
        if (errorMessage.contains("EMAIL NULLS FIRST")) {
            throw new DatabaseException("Email informado já existe");
        }
        if (errorMessage.contains("NAME NULLS FIRST")) {
            throw new DatabaseException("Nome informado já existe");
        }
        if (errorMessage.contains("CPF NULLS FIRST")) {
            throw new DatabaseException("CPF informado já existe");
        }
        if (errorMessage.contains("CNPJ NULLS FIRST")) {
            throw new DatabaseException("CNPJ informado já existe");
        }
        if (errorMessage.contains("REFERENCE NULLS FIRST")) {
            throw new DatabaseException("Código de barras informado já existe");
        }
        if (errorMessage.contains("DEPENDENT_ID")) {
            throw new DatabaseException("Não é possível excluir este(a) "
                    + entity.getTranslatedEntityName().toLowerCase()
                    + " pois existem registros vinculados a ele(a).");
        }

        throw new DatabaseException("Não é possível excluir este(a) "
                + entity.getTranslatedEntityName().toLowerCase()
                + " pois existem dados dependentes no sistema.");
    }

    default void handleResourceNotFound() {
        ENTITY entity = createEntity();
        throw new ResourceNotFoundException(
                entity.getTranslatedEntityName() + " não encontrado(a) para os critérios especificados.");
    }

    /**
     * Verifica se é uma operação do usuário em seus próprios dados
     */
    default boolean isSelfOperation(Long resourceId) {
        Long userId = SecurityContextUtil.getUserId();
        return userId != null && userId.equals(resourceId);
    }

    /**
     * Verifica se o usuário possui a permissão necessária ou se está operando
     * seus próprios dados.
     *
     * @param authority permissão requerida
     * @param resourceId ID do recurso sendo acessado
     * @throws ForbiddenException se usuário não tem permissão necessária
     */
    default void checkUserPermissions(PermissionAuthority authority, Long resourceId) {
        // Se for uma auto-operação (usuário operando seus próprios dados)
        if (isSelfOperation(resourceId)) {
            return; // Permite a operação
        }

        // Caso contrário, verifica permissões normalmente
        checkUserPermissions(authority);
    }

    /**
     * Verifica se o usuário possui a permissão necessária para executar a
     * operação.
     *
     * @param authority permissão requerida
     * @throws ForbiddenException se usuário não tem a permissão necessária
     */
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
