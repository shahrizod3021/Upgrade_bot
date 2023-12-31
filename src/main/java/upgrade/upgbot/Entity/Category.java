package upgrade.upgbot.Entity;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.File;
import upgrade.upgbot.Entity.AbsEntity.AbsNameEntity;

import javax.persistence.Entity;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Category  extends AbsNameEntity {

    private File img;
}
