package upgrade.upgbot.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import upgrade.upgbot.Entity.Liked;

public interface LikedRepository extends JpaRepository<Liked, Integer> {

    Liked findLikedByProductsId(Integer products_id);

    boolean existsLikedByProductsId(Integer products_id);
    Liked findLikedByUsersChatId(Long users_id);

}
