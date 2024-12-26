package rccommerce.services;

import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import rccommerce.dto.OrderDTO;
import rccommerce.dto.OrderItemDTO;
import rccommerce.dto.OrderMinDTO;
import rccommerce.entities.Client;
import rccommerce.entities.Order;
import rccommerce.entities.OrderItem;
import rccommerce.entities.Payment;
import rccommerce.entities.Product;
import rccommerce.entities.User;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.OrderItemRepository;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.SecurityContextUtil;

@Service
public class OrderService implements GenericService<Order, OrderDTO, OrderMinDTO, Long> {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<OrderMinDTO> searchEntity(Pageable pageable) {
        Long userId = SecurityContextUtil.getUserId();
        return findBy(example(null, null, null, userId, null, null, null), false, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrderMinDTO> searchEntity(Long id, String status, String payment, Long userId, String user, Long clientId, String client, Pageable pageable) {
        return findBy(example(id, status, payment, userId, user, clientId, client), pageable);
    }

    //Necessário para atualizar o OrderItem
    // @Override
    // @Transactional
    // public OrderMinDTO insert(OrderDTO dto) {
    //     List<OrderItem> orderItens = new ArrayList<>();
    //     OrderMinDTO orderMinDto = GenericService.super.insert(dto, false);
    //     Order order = new Order(orderMinDto.getId());
    //     for (OrderItemDTO orderItemDto : orderMinDto.getItens()) {
    //         Product product = new Product(orderItemDto.getProductId());
    //         orderItens.add(new OrderItem(order, product, orderItemDto.getQuantity(), orderItemDto.getPrice()));
    //     }
    //     orderItemRepository.saveAllAndFlush(orderItens);
    //     return orderMinDto;
    // }
    @Override
    public JpaRepository<Order, Long> getRepository() {
        return repository;
    }

    @Override
    public void copyDtoToEntity(OrderDTO dto, Order entity) {
        Long userId = SecurityContextUtil.getUserId(); // Obtém o ID do usuário autenticado        
        User user = new User();
        user.setId(userId);
        entity.setUser(user);

        entity.setClient(virifyClient(entity.getUser(), dto));
        entity.setMoment(Instant.now());
        entity.setStatus(OrderStatus.WAITING_PAYMENT);
        entity.setPayment(null);

        for (OrderItemDTO itemDto : dto.getItens()) {
            try {
                Product product = productRepository.getReferenceById(itemDto.getProductId());
                entity.addItens(new OrderItem(entity, product, itemDto.getQuantity(), product.getPrice()));
            } catch (EntityNotFoundException e) {
                throw new ResourceNotFoundException("Produtode id: '" + itemDto.getProductId() + "' não encontrado");
            }
        }
    }

    @Override
    public Order createEntity() {
        return new Order();
    }

    @Override
    public String getTranslatedEntityName() {
        return messageSource.getMessage("entity.Order", null, Locale.getDefault());
    }

    private Example<Order> example(Long id, String status, String movementType, Long userId, String user, Long clientId, String client) {
        Order orderExample = createEntity();
        User userOrder = new User();
        Client clientOrder = new Client();
        Payment entity = new Payment();

        if (id != null) {
            orderExample.setId(id);
        }

        if (userId != null) {
            userOrder.setId(userId);
            orderExample.setUser(userOrder);
        }

        if (clientId != null) {
            clientOrder.setId(clientId);
            orderExample.setClient(clientOrder);
        }

        if (status != null && !status.isEmpty()) {
            orderExample.setStatus(OrderStatus.fromValue(status));
        }
        // if (movementType != null && !movementType.isEmpty()) {
        //     Set<MovementDetail> movementDetails = new HashSet<>();
        //     movementDetails.add(MovementDetail.builder()
        //             .movementType(MovementType.fromValue(movementType))
        //             .build());
        //     entity.setMovementDetails(movementDetails);
        //     orderExample.setPayment(entity);
        // }
        if (user != null && !user.isEmpty()) {
            userOrder.setNameUnaccented(user);
            orderExample.setUser(userOrder);
        }
        if (client != null && !client.isEmpty()) {
            clientOrder.setNameUnaccented(client);
            orderExample.setClient(clientOrder);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact())
                // .withMatcher("movementType", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("user.id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("user.nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("client.id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("client.nameUnaccented", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        return Example.of(orderExample, matcher);
    }

    private Client virifyClient(User user, OrderDTO dto) {

        if (user instanceof Client clientUser) {
            return clientUser;
        }

        if (dto.getClient().getId() == null) {
            Client client = new Client();
            client.setId(4L);
            //return clientRepository.getReferenceById(4L);
            return client;
        }

        return clientRepository.findById(dto.getClient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
    }
}
