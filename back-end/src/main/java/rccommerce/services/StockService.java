package rccommerce.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.StockDTO;
import rccommerce.dto.StockMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.Stock;
import rccommerce.entities.User;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.repositories.UserRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class StockService implements GenericService<Stock, StockDTO, StockMinDTO, Long> {

    @Autowired
    private StockRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional
    public void updateStock(StockDTO dto) {
        Stock entity = createEntity();
        copyDtoToEntity(dto, entity);
        Product product = entity.getProduct();
        product.setQuantity(entity.getQuantity());
        productRepository.save(product);
        repository.save(entity);
    }

    @Override
    public JpaRepository<Stock, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(StockDTO dto, Stock entity) {
        Long userId = SecurityContextUtil.getUserId();
        User user = userRepository.getReferenceById(userId);
        Product product = productRepository.findById(dto.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Produto %s não encontrado", dto.getProduct().getName())));
        entity.setUser(user);
        entity.setProduct(product);
        entity.setMoviment(StockMoviment.valueOf(dto.getMoviment()));
        entity.setMoment(dto.getMoment());
        entity.setQuantity(product.getQuantity());
        entity.setQttMoved(dto.getQttMoved());
    }

    @Override
    public Stock createEntity() {
        return new Stock();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.Stock", null, Locale.getDefault());
    }
}
