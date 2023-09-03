package upgrade.upgbot.Entity;

import jdk.jfr.Enabled;
import lombok.*;
import upgrade.upgbot.Entity.AbsEntity.AbsNameEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
