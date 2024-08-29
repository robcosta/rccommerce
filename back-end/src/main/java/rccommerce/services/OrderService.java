package rccommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.OrderDTO;
import rccommerce.entities.Order;
import rccommerce.repositories.OrderRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository repository;
	
	@Transactional(readOnly = true)
	public OrderDTO findById(Long id) {

		Order result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
		return new OrderDTO(result);
	}
}
