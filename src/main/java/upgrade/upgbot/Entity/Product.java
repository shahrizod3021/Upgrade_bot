package upgrade.upgbot.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.telegram.telegrambots.meta.api.objects.File;
import upgrade.upgbot.Entity.AbsEntity.AbsNameEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product extends AbsNameEntity {

    @Column(name = "product_description", length = 1000)
    private String description;


    @Column(name = "product_price")
    private Double price;

    @Column(name = "product_img")
    private File img;

    @ManyToOne
    private Category category;
}
