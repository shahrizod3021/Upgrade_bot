package upgrade.upgbot.Entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import upgrade.upgbot.Entity.AbsEntity.AbsNameEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "orders")
public class Order extends AbsNameEntity {

    @ManyToOne
    @JoinColumn(name = "ordered_product")
    private Product product;

    @Column(name = "phone_number")
    private String phoneNumber;


}
