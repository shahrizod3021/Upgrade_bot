package upgrade.upgbot.Entity;

import lombok.*;
import org.springframework.data.repository.query.SpelQueryContext;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Liked {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany
    private List<Product> products;

    @OneToMany
    private List<User> users;
}
