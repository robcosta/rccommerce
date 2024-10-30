package rccommerce.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.entities.Product;
import rccommerce.entities.Stock;
import rccommerce.entities.User;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.ProductRepository;
import rccommerce.repositories.StockRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class StockService {

    @Autowired
    private StockRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void saveteStock(User user, Product product, Instant moment, Double qttMoved, StockMoviment moviment, Product product2) {
        Stock stock = new Stock();
        try {
            product = productRepository.getReferenceById(product.getId());
            stock.setUser(user);
            stock.setProduct(product);
            stock.setMoment(moment);
            stock.setQttMoved(qttMoved);
            stock.setMoviment(moviment);
            product.setQuantity(stock.getQuantity());
            productRepository.saveAndFlush(product);
            repository.saveAndFlush(stock);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
    }
}
