package rccommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rccommerce.entities.ProductCategory;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, Long> {

//	@Query("SELECT obj FROM Category obj "
//			+ "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%', :name,'%')) ")
//	Page<Category> searchAll(String name, Pageable pageable);
//
//	Optional<Category> findByName(String name);
}
