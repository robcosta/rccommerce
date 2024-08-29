package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
