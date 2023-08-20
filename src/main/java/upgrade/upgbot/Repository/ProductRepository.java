package upgrade.upgbot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import upgrade.upgbot.Entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findProductByCategoryId(Integer category_id);


    @Query(value = "select * from product where name like %?%", nativeQuery = true)
    List<Product> findProductByName(String name);

}
