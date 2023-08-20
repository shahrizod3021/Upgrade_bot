package upgrade.upgbot.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.repository.query.SpelQueryContext;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Product> products;

    @OneToMany(fetch = FetchType.EAGER)
    private List<User> users;
}
