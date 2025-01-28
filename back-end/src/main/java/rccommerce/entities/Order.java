package rccommerce.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
import rccommerce.util.BigDecimalTwoDecimalSerializer;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "tb_order", indexes = {
    @Index(name = "idx_order_moment", columnList = "moment")})
public class Order implements Convertible<OrderDTO, OrderMinDTO> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private Payment payment;

    @Builder.Default
    @OneToMany(mappedBy = "id.order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderItem> itens = new HashSet<>();

    public Order(Long id) {
        this.id = id;
    }

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

    //Retorna o valor total do pedido
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    public BigDecimal getTotalOrder() {
        return itens.stream()
                .map(item -> item.getPrice().multiply(item.getQuantity())) // Multiplica preço pela quantidade
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma os resultados
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
