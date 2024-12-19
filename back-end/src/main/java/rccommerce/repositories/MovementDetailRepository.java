package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.MovementDetail;

@Repository
public interface MovementDetailRepository extends JpaRepository<MovementDetail, Long> {

}
