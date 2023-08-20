package upgrade.upgbot.Repository;

import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import upgrade.upgbot.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {


}
