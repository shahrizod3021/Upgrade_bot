package upgrade.upgbot.Component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import upgrade.upgbot.Entity.Liked;
import upgrade.upgbot.Entity.User;
import upgrade.upgbot.Repository.LikedRepository;
import upgrade.upgbot.Repository.UserRepository;

import java.util.Collections;

@Component
@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final LikedRepository likedRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String init;

    @Override
    public void run(String... args) throws Exception {
        if (init.equals("create-drop") || init.equals("create")) {
            User user = new User(1681505177L, "+998 88 581 06 30");
            user.setName("Admin");
            User save = userRepository.save(
                    user
            );
            Liked liked = Liked.builder().users(Collections.singletonList(save)).build();
            likedRepository.save(liked);
        }
    }
}
