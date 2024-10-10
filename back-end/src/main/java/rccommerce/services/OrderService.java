package rccommerce.services;

import java.time.Instant;

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
import rccommerce.entities.User;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.Very;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.OrderItemRepository;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.util.VerifyService;

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
	
	@Autowired
	private VerifyService VerifyService;
	
	@Transactional(readOnly = true)
	public OrderDTO findById(Long id) {
		VerifyService.veryUser(Very.READER, null);
		Order result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
		return new OrderDTO(result);
	}
	
	@Transactional
	public OrderDTO insert(OrderDTO dto) {
		Order order = new Order();		
		User user = userService.authenticated();
		Client client = virifyClient(user, dto);
		VerifyService.veryUser(Very.CREATE, client.getId());		
		
		order.setUser(user);
		order.setMoment(Instant.now());
		order.setStatus(OrderStatus.WAITING_PAYMENT);

		
		order.setClient(client);
		
		for(OrderItemDTO itemDto : dto.getItems()) {
			try {
				Product product = productRepository.getReferenceById(itemDto.getProductId());
				OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
				order.getItems().add(item);
			}catch (EntityNotFoundException e) {
				throw new ResourceNotFoundException("Produto não encontrado");
			}
		}
		
		repository.save(order);
		orderItemRepository.saveAll(order.getItems());
		return new OrderDTO(order);
	}

	private Client virifyClient(User user, OrderDTO dto) {
		if(user instanceof Client) {
			return (Client) user;
		}
				
		if(dto.getClient().getId() == null) {
			return clientRepository.getReferenceById(4L);
		}
	
		return  clientRepository.findById(dto.getClient().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
	}
}