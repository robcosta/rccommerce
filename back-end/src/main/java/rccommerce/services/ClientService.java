package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.ClientDTO;
import rccommerce.dto.ClientFullDTO;
import rccommerce.dto.ClientMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.repositories.ClientRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;

@Service
public class ClientService implements GenericService<Client, ClientDTO, ClientMinDTO, Long> {

    @Autowired
    private ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<ClientMinDTO> searchEntity(Long id, String name, String email, String cpf, Pageable pageable) {
        Page<Client> result = repository.searchAll(
                id,
                AccentUtils.removeAccents(name),
                AccentUtils.removeAccents(email),
                cpf,
                pageable);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cliente encontrado para estes critérios de busca.");
        }
        return result.map(ClientMinDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientFullDTO findByIdWithAddresses(Long id) {
        checkUserPermissions(PermissionAuthority.PERMISSION_READER, id);
        Client result = repository.findByIdWithAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        return new ClientFullDTO(result);
    }

    @Override
    public JpaRepository<Client, Long> getRepository() {
        return repository;
    }

    @Override
    public Client createEntity() {
        return new Client();
    }
}
