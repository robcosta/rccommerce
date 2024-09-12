package rccommerce.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rccommerce.dto.PaymentDTO;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.Stock;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.entities.enums.StockMoviment;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.PaymentRepository;
import rccommerce.services.exceptions.InvalidArgumentExecption;
import rccommerce.services.exceptions.ResourceNotFoundException;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository repository;

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductService productService;
	
	@Transactional(readOnly = true)
	public PaymentDTO findById(Long id) {

		Payment result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
		return new PaymentDTO(result);
	}

	@Transactional
	public PaymentDTO insert(PaymentDTO dto) {
		
		Payment payment = new Payment();
		Order order = orderRepository.findById(dto.getOrderId())
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
		
		
		if(order.getPayment() != null){
			throw new InvalidArgumentExecption("Pedido já pago");
		}
				
		payment.setMoment(Instant.now());
		payment.setPaymentType(dto.getPaymentType());
		payment.setOrder(order);
		
		order.setStatus(OrderStatus.PAID);
		
		List<Stock> productStock = new ArrayList<>();
		for(OrderItem item : order.getItems()) {
			Stock stock = new Stock();
			stock.setUser(order.getUser());
			stock.setProduct(item.getProduct());
			stock.setMoment(payment.getMoment());
			stock.setMoviment(StockMoviment.SALE);
			stock.setQuantity(item.getProduct().getQuantity());
			stock.setQttMoved(item.getQuantity());
			
			productStock.add(stock);
		}
		
		payment = repository.save(payment);
		order.setPayment(payment);
		orderRepository.save(order);
		productService.updateStock(productStock);
				
		return new PaymentDTO(payment);
	}
}
