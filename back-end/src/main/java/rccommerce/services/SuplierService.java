package rccommerce.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.AddressDTO;
import rccommerce.dto.SuplierDTO;
import rccommerce.dto.SuplierFullDTO;
import rccommerce.dto.SuplierMinDTO;
import rccommerce.entities.Address;
import rccommerce.entities.Suplier;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
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
        Page<Suplier> result = repository.searchAll(
                id,
                AccentUtils.removeAccents(name),
                cnpj,
                pageable);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum fornecedor encontrado para estes critérios de busca.");
        }
        return result.map(SuplierMinDTO::new);
    }

    @Transactional(readOnly = true)
    public SuplierFullDTO findByIdWithAddresses(Long id) {
        Suplier result = repository.findByIdWithAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + id));
        return new SuplierFullDTO(result);
    }

    @Override
    public void copyDtoToEntity(SuplierDTO dto, Suplier entity) {
        entity.setName(dto.getName());
        entity.setCnpj(dto.getCnpj());

        entity.getAddresses().clear();
        for (AddressDTO AdressDTO : dto.getAddresses()) {
            Address address = Address.builder()
                    .street(AdressDTO.getStreet())
                    .number(AdressDTO.getNumber())
                    .complement(AdressDTO.getComplement())
                    .district(AdressDTO.getDistrict())
                    .city(AdressDTO.getCity())
                    .state(AdressDTO.getState())
                    .zipCode(AdressDTO.getZipCode())
                    .build();
            entity.addAddresses(address);
        }
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
}
