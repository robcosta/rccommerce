package rccommerce.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.OrderDTO;
import rccommerce.dto.OrderItemDTO;
import rccommerce.entities.Client;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Product;
import rccommerce.entities.Stock;
import rccommerce.entities.User;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.OrderItemRepository;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository repository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional(readOnly = true)
	public OrderDTO findById(Long id) {

		Order result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
		return new OrderDTO(result);
	}
	
	@Transactional
	public OrderDTO insert(OrderDTO dto) {
//		Map<Product, Double> productQttMap = new HashMap<>();
		Order order = new Order();
		User user = userService.authenticated();
		Client client = new Client();
		
		order.setUser(user);
					
		order.setMoment(Instant.now());
		
		order.setStatus(OrderStatus.WAITING_PAYMENT);
		
		if(dto.getClient().getId() == null) {
			client = clientRepository.findById(1L).get();
		} else {
			client = clientRepository.findById(dto.getClient().getId())
					.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
		}
		order.setClient(client);
		
		for(OrderItemDTO itemDto : dto.getItems()) {
			Product product;
			try {
				product = productRepository.getReferenceById(itemDto.getProductId());
				OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());				
				order.getItems().add(item);	
			}catch (EntityNotFoundException e) {
				throw new ResourceNotFoundException("Produto não encontrado");
			}

//			productQttMap.merge(product, itemDto.getQuantity(), Double::sum);
		}
		
		order = repository.saveAndFlush(order);
		for(OrderItem item: order.getItems()){			
			orderItemRepository.saveAndFlush(item);
		}
		
		return new OrderDTO(order);

		
		
//		orderItemRepository.saveAll(order.getItems());
//		updateStock(user,order.getMoment(),productQttMap);
//		return new OrderDTO(order);
	}
	
	
	@Transactional
	private void updateStock(User user, Instant moment, Map<Product, Double> items) {
		Stock stock = new Stock();
		stock.setUser(user);
		stock.setMoment(moment);
		stock.setMoviment(StockMoviment.SALE);
		for(Map.Entry<Product, Double> result : items.entrySet()) {
			stock.setProduct(result.getKey());
			stock.setQttMoved(result.getValue());		
		}		
	}
	
	
	  public static void main(String[] args) {

	        Map<String, Integer> hasMap = new HashMap<>();  
	        hasMap.put("A", 1);

	        System.out.println(hasMap);

	        hasMap.merge("A", 1, Integer::sum);
	        hasMap.merge("B", 1, Integer::sum);
	        hasMap.merge("A", 1, Integer::sum);
	        hasMap.merge("B", 4, Integer::sum);

	        System.out.println(hasMap); 
	        
	  
	   
	    }
	  
	  

}
