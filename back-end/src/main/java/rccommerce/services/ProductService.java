package rccommerce.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.ProductDTO;
import rccommerce.dto.ProductMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.ProductCategory;
import rccommerce.entities.ProductStock;
import rccommerce.entities.Suplier;
import rccommerce.entities.User;
import rccommerce.entities.enums.PermissionAuthority;
import rccommerce.entities.enums.StockMovement;
import rccommerce.repositories.ProductCategoryRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.repositories.SuplierRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;
import rccommerce.services.util.ConvertString;
import rccommerce.services.util.SecurityContextUtil;

@Profile("!disabled")
@Service
public class ProductService implements GenericService<Product, ProductDTO, ProductMinDTO, Long> {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private SuplierRepository suplierRepository;

    @Autowired
    private StockRepository stockRepository;

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
    public ProductDTO findByReference(String codBarra) {
        codBarra = ("0000000000000" + codBarra).formatted().substring(codBarra.length());

        Product result = repository.findByReference(codBarra)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));                
        return new ProductDTO(result);
    }

    @Transactional
    public ProductMinDTO insert(ProductDTO dto) {
        checkUserPermissions(PermissionAuthority.PERMISSION_CREATE);
        Product entity = new Product();
        return saveOrUpdateProduct(entity, dto);
    }

    // @Transactional
    // public void updateStock(List<ProductStock> stocks) {
    //     for (ProductStock productStock : stocks) {
    //         try {
    //             Product entity = repository.getReferenceById(productStock.getProduct().getId());
    //             entity.setQuantity(productStock.getQuantity());
    //             repository.save(entity);
    //         } catch (EntityNotFoundException e) {
    //             throw new ResourceNotFoundException("Produto não encontrado");
    //         }
    //     }
    //     stockRepository.saveAll(stocks);
    // }

    @Transactional
    public ProductMinDTO update(ProductDTO dto, Long id) {
        return update(dto, id, true);
    }

    @Transactional
    public ProductMinDTO update(ProductDTO dto, Long id, boolean checkPermission) {
        if (checkPermission) {
            checkUserPermissions(PermissionAuthority.PERMISSION_UPDATE, id);
        }

        try {
            Product entity = repository.getReferenceById(id);
            return saveOrUpdateProduct(entity, dto);
        } catch (EntityNotFoundException e) {
            handleResourceNotFound();
            return null;
        }
    }

    private ProductMinDTO saveOrUpdateProduct(Product entity, ProductDTO dto) {
        entity = entity.convertEntity(dto);
        entity.setReference(getReference(dto, entity));

        // Verifica se o fornecedor existe
        if (dto.getSuplier().getId() == null) {
            throw new InvalidArgumentExecption("Fornecedor não informado");
        }
        Suplier suplier = suplierRepository.findById(dto.getSuplier().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
        entity.setSuplier(suplier);

        // Verifica se a categoria existe
        if (dto.getCategories().isEmpty()) {
            throw new InvalidArgumentExecption("Categoria não informada");
        }
        List<ProductCategory> categories = categoryRepository.findAll();
        Set<ProductCategory> categorySet = dto.getCategories().stream()
                .map(categoryDTO -> categories.stream()
                        .filter(c -> c.getId().equals(categoryDTO.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Categoria " + categoryDTO.getId() + " não encontrada")))
                .collect(Collectors.toSet());
        entity.setCategories(categorySet);

        try {
            entity = repository.saveAndFlush(entity);
            // checkUserPermissions(PermissionAuthority.PERMISSION_STOCK);
            if (dto.getQuantity() != null) {
                ProductStock stock = ProductStock.builder()
                        .movement(StockMovement.INPUT)
                        .moment(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                        .user(new User(getOperatorId()))
                        .quantity(BigDecimal.ZERO)
                        .build();
                stock.setProduct(entity);
                stock.setQttMoved(dto.getQuantity());
                stockRepository.save(stock);
            }
            return new ProductMinDTO(entity);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e);
            return null;
        }
    }

    /*
     * Gera o código de barras EAN13 para o produto
     */
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

    /**
     * Retorna o id do operador logado na seção. Boolean.
     */
    private Long getOperatorId() {
        return SecurityContextUtil.getUserId();
    }

    @Override
    public JpaRepository<Product, Long> getRepository() {
        return repository;
    }

    @Override
    public Product createEntity() {
        return new Product();
    }
}
