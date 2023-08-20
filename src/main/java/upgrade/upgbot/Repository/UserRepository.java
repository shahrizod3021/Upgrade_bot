package upgrade.upgbot.Repository;

import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import upgrade.upgbot.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsUserByChatId(Long id);

    User findUserByChatId(Long chatId);
}
