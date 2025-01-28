package rccommerce.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import rccommerce.entities.Product;
import rccommerce.entities.User;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.repositories.ClientRepository;
import rccommerce.repositories.OrderRepository;
import rccommerce.repositories.ProductRepository;
import rccommerce.services.exceptions.ResourceNotFoundException;
import rccommerce.services.interfaces.GenericService;
import rccommerce.services.util.AccentUtils;
import rccommerce.services.util.ConvertString;
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
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<OrderMinDTO> searchEntity(Pageable pageable) {
        String userId = SecurityContextUtil.getUserId().toString();
        return searchEntity(null, userId, "", "", null, "", null, null, pageable);
    }

    // @Transactional(readOnly = true)
    // public Page<OrderMinDTO> searchEntity(
    //         String id,
    //         String userid,
    //         String username,
    //         String clientId,
    //         String clientname,
    //         // String status,
    //         // String timeStart,
    //         // String timeEnd,
    //         Pageable pageable) {
    //     Instant timeStart = Instant.parse("2023-01-01T04:00:00Z"); // ISO-8601
    //     Page<Order> result = repository.findOrder(
    //             ConvertString.parseLongOrNull(id),
    //             ConvertString.parseLongOrNull(userid),
    //             AccentUtils.removeAccents(username),
    //             ConvertString.parseLongOrNull(clientId),
    //             AccentUtils.removeAccents(clientname),
    //             // (status == null || status.isEmpty()) ? null : OrderStatus.fromValue(status).getName(),
    //             timeStart,
    //             pageable
    //     );
    @Transactional(readOnly = true)
    public Page<OrderMinDTO> searchEntity(
            String id,
            String userId,
            String status,
            String userName,
            String clientId,
            String clientName,
            String timeStart,
            String timeEnd,
            Pageable pageable) {

        // String timeStart = Instant.parse("2023-01-01T04:00:00Z").toString(); // ISO-8601
        // String time = ConvertString.parseDateOrNull(timeStart).toString(); // ISO-8601
        Page<Order> result = repository.findOrder(
                ConvertString.parseLongOrNull(id),
                ConvertString.parseLongOrNull(userId),
                status == null || status.isEmpty() ? "" : OrderStatus.fromValue(status).getName(),
                AccentUtils.removeAccents(userName).toUpperCase(),
                ConvertString.parseLongOrNull(clientId),
                AccentUtils.removeAccents(clientName).toUpperCase(),
                ConvertString.parseDateOrNull(timeStart),
                ConvertString.parseDateOrNull(timeEnd),
                pageable
        );

        if (result.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Nenhum pedido encontrado para estes critérios de busca.");
        }

        return result.map(OrderMinDTO::new);
    }

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
        entity.setMoment(Instant.now().truncatedTo(ChronoUnit.SECONDS));
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

    private Client virifyClient(User user, OrderDTO dto) {

        if (user instanceof Client clientUser) {
            return clientUser;
        }

        if (dto.getClient().getId() == null) {
            Client client = new Client();
            client.setId(4L);
            return client;
        }

        return clientRepository.findById(dto.getClient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
    }
}
