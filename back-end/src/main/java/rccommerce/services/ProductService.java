package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.TaxConfigurationDTO;
import rccommerce.dto.fulldto.ProductFullDTO;
import rccommerce.dto.mindto.ProductMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.entities.ProductStock;
import rccommerce.entities.Suplier;
import rccommerce.entities.TaxConfiguration;
import rccommerce.entities.enums.TaxType;
import rccommerce.repositories.CategoryRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;
import rccommerce.services.util.ConvertString;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class ProductService implements GenericService<Product, ProductDTO, ProductMinDTO, Long> {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SuplierRepository suplierRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public ProductFullDTO findByIdFull(Long id) {
        Product result = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        return new ProductFullDTO(result); 
    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> searchEntity(String id, String name, String reference, String suplierId, String categoryId, Pageable pageable) {
        Page<Product> result = repository.findProduct(
                ConvertString.parseLongOrNull(id),
                AccentUtils.removeAccents(name),
                reference,
                ConvertString.parseLongOrNull(suplierId),
                ConvertString.parseLongOrNull(categoryId),
                pageable);
        if (result.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Nenhum produto encontrado para estes critérios de busca.");
        }
        return result.map(ProductMinDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductMinDTO findByReference(String codBarra) {
        codBarra = ("0000000000000" + codBarra).formatted().substring(codBarra.length());

        Product result = repository.findByReference(codBarra)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
        return new ProductMinDTO(result);
    }

    @Transactional
    public void updateStock(List<ProductStock> stocks) {
        for (ProductStock productStock : stocks) {
            try {
                Product entity = repository.getReferenceById(productStock.getProduct().getId());
                entity.setQuantity(productStock.getQuantity());
                repository.save(entity);
            } catch (EntityNotFoundException e) {
                throw new ResourceNotFoundException("Produto não encontrado");
            }
        }
        stockRepository.saveAll(stocks);
    }

    @Override
    public void copyDtoToEntity(ProductDTO dto, Product entity) {
        copyBasicInfo(dto, entity);
        copyCategories(dto, entity);
        copySuplier(dto, entity);
        copyTaxes(dto, entity);
        updateAuditInfo(entity);
    }

    private void copyBasicInfo(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setUn(dto.getUnit());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setQuantity(BigDecimal.ZERO);
        entity.setReference(getReference(dto, entity));
    }

    private void copyCategories(ProductDTO dto, Product entity) {
        entity.getCategories().clear();
        dto.getCategories().forEach(categoryDTO -> {
            ProductCategory category = categoryRepository.findById(categoryDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Categoria %d não encontrada", categoryDTO.getId())));
            entity.addCategory(category);
        });
    }

    private void copySuplier(ProductDTO dto, Product entity) {
        if (dto.getSuplier() == null) {
            entity.setSuplier(getDefaultSuplier());
            return;
        }

        Suplier suplier = suplierRepository.findById(dto.getSuplier().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Fornecedor %d não encontrado", dto.getSuplier().getId())));
        entity.setSuplier(suplier);
    }

    private Suplier getDefaultSuplier() {
        return suplierRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor padrão não encontrado"));
    }

    private void copyTaxes(ProductDTO dto, Product entity) {
        copyInputTax(dto, entity);
        copyOutputTax(dto, entity);
    }

    private void copyInputTax(ProductDTO dto, Product entity) {
        if (dto.getInputTax() != null) {
            TaxConfiguration inputTax = getOrCreateTax(entity.getInputTax(), TaxType.INPUT);
            copyTaxDtoToEntity(dto.getInputTax(), inputTax);
            entity.setInputTax(inputTax);
        }
    }

    private void copyOutputTax(ProductDTO dto, Product entity) {
        if (dto.getOutputTax() != null) {
            TaxConfiguration outputTax = getOrCreateTax(entity.getOutputTax(), TaxType.OUTPUT);
            copyTaxDtoToEntity(dto.getOutputTax(), outputTax);
            entity.setOutputTax(outputTax);
        }
    }

    private TaxConfiguration getOrCreateTax(TaxConfiguration tax, TaxType type) {
        if (tax == null) {
            return TaxConfiguration.builder()
                    .type(type)
                    .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                    .createdBy(SecurityContextUtil.getUserId())
                    .build();
        }
        tax.setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        return tax;
    }

    private void updateAuditInfo(Product entity) {
        if (entity.getId() == null) {
            entity.setCreatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            entity.setCreatedBy(SecurityContextUtil.getUserId());
        }
        entity.setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    }

    private void copyTaxDtoToEntity(TaxConfigurationDTO dto, TaxConfiguration entity) {
        // CSTs e CSOSN
        entity.setCstIcms(dto.getCstIcms());
        entity.setCstPis(dto.getCstPis());
        entity.setCstCofins(dto.getCstCofins());
        entity.setCstIpi(dto.getCstIpi());
        entity.setCsosn(dto.getCsosn());
        
        // Alíquotas
        entity.setIcms(dto.getIcms());
        entity.setIpi(dto.getIpi());
        entity.setPis(dto.getPis());
        entity.setCofins(dto.getCofins());
        entity.setIcmsSt(dto.getIcmsSt());
        
        // Bases de cálculo
        entity.setPisBase(dto.getPisBase());
        entity.setCofinsBase(dto.getCofinsBase());
        entity.setIcmsBase(dto.getIcmsBase());
        entity.setIcmsStBase(dto.getIcmsStBase());
        entity.setIpiBase(dto.getIpiBase());
        
        // Códigos fiscais e classificações
        entity.setNcm(dto.getNcm());
        entity.setCest(dto.getCest());
        entity.setCfop(dto.getCfop());
        entity.setTipi(dto.getTipi());
        
        // Outros campos fiscais
        entity.setIcmsOrigem(dto.getIcmsOrigem());
        entity.setMva(dto.getMva());
        entity.setTipoCalculoIcms(dto.getTipoCalculoIcms());
        entity.setEnquadramentoIpi(dto.getEnquadramentoIpi());
        entity.setReducaoBase(dto.getReducaoBase());
        entity.setDiferimento(dto.getDiferimento());
        entity.setEan(dto.getEan());
    }

    private String getReference(ProductDTO dto, Product entity) {
        String barCode = isValidBarCodeEAN(dto.getReference());

        if (entity.getId() != null && barCode.isEmpty()) {
            return generateEAN13(entity.getId());
        }

        if (barCode.isEmpty()) {
            return generateEAN13(repository.count() + 1L);
        }

        return barCode;
    }

    private String generateEAN13(Long id) {
        String codEan13 = id.toString();
        codEan13 = ("000000000000" + codEan13).formatted().substring(codEan13.length());
        int digit = checkDigit(codEan13);

        return codEan13 + Integer.toString(digit);
    }

    private String isValidBarCodeEAN(String barCode) {
        int digit;

        if (barCode.isEmpty()) {
            return "";
        }

        if (barCode.length() != 13) {
            throw new InvalidArgumentExecption("Código de barras inválido");
        }

        digit = checkDigit(barCode);
        if (Integer.parseInt(barCode.substring(12)) != digit) {
            throw new InvalidArgumentExecption("Código de barras inválido");
        }
        return barCode;
    }

    private int checkDigit(String codEan) {
        int digit;
        int sum = 0;
        int size = codEan.length();
        String checkSum = "131313131313";

        if (size == 13) {
            size--;
        }
        try {
            Long.valueOf(codEan);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentExecption("Código de barras inválido");
        }

        for (int i = 0; i < size; i++) {
            sum += Character.getNumericValue(codEan.charAt(i)) * Character.getNumericValue(checkSum.charAt(i));
        }

        digit = 10 - (sum % 10);
        return digit == 10 ? 0 : digit;
    }

    @Override
    public JpaRepository<Product, Long> getRepository() {
        return repository;
    }

    @Override
    public Product createEntity() {
        return new Product();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.Product", null, Locale.getDefault());
    }
}
