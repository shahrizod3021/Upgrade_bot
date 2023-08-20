package upgrade.upgbot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upgrade.upgbot.Entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
