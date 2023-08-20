package upgrade.upgbot.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import upgrade.upgbot.Entity.AbsEntity.AbsNameEntity;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
public class User extends AbsNameEntity {

    @Column(name = "chat_id")
    private Long chatId;


    @Column(name = "user_phone_number")
    private String phoneNumber;



}
