package rccommerce.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rccommerce.dto.OrderDTO;
import rccommerce.dto.OrderMinDTO;
import rccommerce.entities.enums.OrderStatus;
import rccommerce.services.interfaces.Convertible;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_order")
public class Order implements Convertible<OrderDTO, OrderMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "id.order")
    private Set<OrderItem> itens = new HashSet<>();

    public Order(Long id, Instant moment, OrderStatus status, User user, Client client, Payment payment) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.user = user;
        this.client = client;
        this.payment = payment;
    }

    public void addItens(OrderItem orderItem) {
        itens.add(orderItem);
    }

    public List<Product> getProducts() {
        return itens.stream().map(x -> x.getProduct()).toList();
    }

    @Override
    public OrderDTO convertDTO() {
        return new OrderDTO(this);
    }

    @Override
    public OrderMinDTO convertMinDTO() {
        return new OrderMinDTO(this);
    }
}
