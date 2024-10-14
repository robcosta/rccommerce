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

	String getClassName();

	String getTranslatedEntityName();

	@Transactional(readOnly = true)
	default Page<MINDTO> findAll(Pageable pageable) {
		checkUserPermissions(PermissionAuthority.PERMISSION_READER, null, getClassName());

		Page<T> result = getRepository().findAll(pageable);
		if (result.getContent().isEmpty()) {
			handleResourceNotFound();
		}
		return result.map(x -> x.convertMinDTO());
	}

	@Transactional(readOnly = true)
	default Page<MINDTO> searchAll(Example<T> example, Pageable pageable) {
		checkUserPermissions(PermissionAuthority.PERMISSION_READER, null, getClassName());

		Page<T> result = getRepository().findAll(example, pageable);
		if (result.isEmpty()) {
			handleResourceNotFound();
		}
		return result.map(x -> x.convertMinDTO());
	}

	@Transactional(readOnly = true)
	default MINDTO findById(ID id) {
		checkUserPermissions(PermissionAuthority.PERMISSION_READER, (Long) id, getClassName());

		T result = getRepository().findById(id).orElseThrow(() -> {
			handleResourceNotFound();
			return null;
		});

		return result.convertMinDTO();
	}

	@Transactional(readOnly = true)
	default Page<MINDTO> findBy(Example<T> example, Pageable pageable) {
		checkUserPermissions(PermissionAuthority.PERMISSION_READER, null, getClassName());

		Page<T> result = getRepository().findBy(example, query -> query.page(pageable));

		if (result.getContent().isEmpty()) {
			handleResourceNotFound();
		}
		return result.map(x -> x.convertMinDTO());
	}

	@Transactional
	default MINDTO insert(DTO dto) {
		checkUserPermissions(PermissionAuthority.PERMISSION_CREATE, null, getClassName());

		T entity = createEntity();
		copyDtoToEntity(dto, entity);
		try {
			entity = getRepository().saveAndFlush(entity);
			return entity.convertMinDTO();
		} catch (DataIntegrityViolationException e) {
			handleDataIntegrityViolation(e);
			return null;
		}
	}

	@Transactional
	default MINDTO update(DTO dto, ID id) {
		checkUserPermissions(PermissionAuthority.PERMISSION_UPDATE, (Long) id, getClassName());

		try {
			T entity = getRepository().getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = getRepository().saveAndFlush(entity);
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
		checkUserPermissions(PermissionAuthority.PERMISSION_DELETE, (Long) id, getClassName());

		if (!getRepository().existsById(id)) {
			handleResourceNotFound();
		}
		try {
			getRepository().deleteById(id);
		} catch (DataIntegrityViolationException e) {
			handleDataIntegrityViolation(e);
		}
	}

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
		throw new DatabaseException(getTranslatedEntityName() + " com vínculos em outras tabelas, exclusão proibida");
	}

	// Método auxiliar para lançar a exceção
	default void handleResourceNotFound() {
		throw new ResourceNotFoundException(
				getTranslatedEntityName() + " não encontrado para os critérios especificados.");
	}

	// Método auxiliar que verifica a permissão do usuário para acesso aos métodos
	// do service.
	default void checkUserPermissions(PermissionAuthority authority, Long id, String className) {
		Long userId = SecurityContextUtil.getUserId(); // Obtém o ID do usuário autenticado
		List<String> authList = SecurityContextUtil.getAuthList(); // Obtém a lista de permissões do usuário

		// Permite auto operações.
		if (userId.equals(id)) {
			if (className.equalsIgnoreCase("rccommerce.services.ClientService")) {
				return;
			}
			if (className.equalsIgnoreCase("rccommerce.services.OperatorService")) {
				return;
			}
		}

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
