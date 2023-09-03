package upgrade.upgbot.Entity;

import lombok.*;
import upgrade.upgbot.Entity.AbsEntity.AbsNameEntity;

import javax.persistence.Column;
import javax.persistence.Entity;


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
