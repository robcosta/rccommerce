package rccommerce.services;

import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import rccommerce.dto.StockDTO;
import rccommerce.dto.StockMinDTO;
import rccommerce.entities.Product;
import rccommerce.entities.Stock;
import rccommerce.entities.User;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class StockService implements GenericService<Stock, StockDTO, StockMinDTO, Long> {

    @Autowired
    private StockRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MessageSource messageSource;

    // @Transactional
    // public StockMinDTO updateStock(Stock entity, boolean checkpermisson) {
    //     StockMinDTO stock = GenericService.super.insert(new StockDTO(entity), checkpermisson);
    //     //Atualiza a quantidade de produtos
    //     try {
    //         Product product = productRepository.getReferenceById(dto.getProduct().getId());
    //         product.setQuantity(stock.getQuantity());
    //         productRepository.saveAndFlush(product);
    //     } catch (EntityNotFoundException e) {
    //         throw new ResourceNotFoundException("Produto não encontrado");
    //     }
    //     return null;
    // }
    //     Stock stock = new Stock();
    //     try {
    //         product = productRepository.getReferenceById(product.getId());
    //         stock.setUser(user);
    //         stock.setProduct(product);
    //         stock.setMoment(moment);
    //         stock.setQttMoved(qttMoved);
    //         stock.setMoviment(moviment);
    //         product.setQuantity(stock.getQuantity());
    //         productRepository.saveAndFlush(product);
    //         repository.saveAndFlush(stock);
    //     } catch (EntityNotFoundException e) {
    //         throw new ResourceNotFoundException("Produto não encontrado");
    //     }
    // }
    @Override
    public JpaRepository<Stock, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(StockDTO dto, Stock entity) {
        entity.setUser(new User(SecurityContextUtil.getUserId()));
        entity.setProduct(new Product(dto.getProduct().getId()));
        entity.setMoment(Instant.now());
        entity.setQttMoved(dto.getQttMoved());
        entity.setQuantity(dto.getQuantity());
        entity.setMoviment(StockMoviment.valueOf(dto.getMoviment()));
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
