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

import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.entities.enums.RoleAuthority;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.PermissionRepository;
import rccommerce.repositories.RoleRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;
import rccommerce.services.util.SecurityContextUtil;
import rccommerce.services.util.ValidPassword;

@Service
public class ClientService implements GenericService<Client, ClientDTO, ClientMinDTO, Long> {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<ClientMinDTO> searchEntity(Long id, String name, String email, String cpf, Pageable pageable) {
        return findBy(example(id, name, email, cpf), pageable);
    }

    @Override
    public JpaRepository<Client, Long> getRepository() {
        return repository;
    }

    @Override
    public Client createEntity() {
        return new Client();
    }

    @Override
    public void checkUserPermissions(PermissionAuthority authority, Long id) {
        Long userId = SecurityContextUtil.getUserId(); // Obtém o ID do usuário autenticado

        //Verifica se o usuário é o próprio cliente para permitir auto operações
        if (userId.equals(id)) {
            return;
        }
        // Permite que qualquer usuário se cadastre como Cliente
        if (authority.equals(PermissionAuthority.PERMISSION_CREATE)) {
            return;
        }

        // Para os demais casos, chama o método da interface GenericService
        GenericService.super.checkUserPermissions(authority);
    }

    @Override
    public void copyDtoToEntity(ClientDTO dto, Client entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail().toLowerCase());
        if (!dto.getPassword().isEmpty()) {
            entity.setPassword(ValidPassword.isValidPassword(dto.getPassword()));
        }
        entity.setCpf(dto.getCpf());
        entity.getRoles().clear();
        entity.addRole(roleRepository.findByAuthority(RoleAuthority.ROLE_CLIENT.getName()));
        entity.getPermissions().clear();
        entity.addPermission(permissionRepository.findByAuthority(PermissionAuthority.PERMISSION_NONE.getName()));
    }

    @Override
    public String getTranslatedEntityName() {
        // Pega a tradução do nome da entidade "Client" para aplicar nas mensagens de erro
        return messageSource.getMessage("entity.Client", null, Locale.getDefault());
    }

    private Example<Client> example(Long id, String name, String email, String cpf) {
        Client clientExample = createEntity();
        if (id != null) {
            clientExample.setId(id);
        }
        if (name != null && !name.isEmpty()) {
            clientExample.setNameUnaccented(AccentUtils.removeAccents(name));
        }
        if (email != null && !email.isEmpty()) {
            clientExample.setEmail(AccentUtils.removeAccents(email));
        }
        if (cpf != null && !cpf.isEmpty()) {
            clientExample.setCpf(cpf);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.exact());

        return Example.of(clientExample, matcher);
    }
}
