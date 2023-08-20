package upgrade.upgbot.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import upgrade.upgbot.Entity.Liked;
import upgrade.upgbot.Entity.User;
import upgrade.upgbot.Repository.LikedRepository;
import upgrade.upgbot.Repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SendMessageService sendMessageService;

    private final LikedRepository likedRepository;

    public SendMessage addUser(Long chatId, String name, String phoneNumber) {
        User build = User.builder()
                .phoneNumber(phoneNumber)
                .chatId(chatId)
                .build();
        build.setName(name);
        User save = userRepository.save(build);
        Liked liked = Liked.builder().users(Collections.singletonList(save)).build();
        likedRepository.save(liked);
        return sendMessageService.sendMessage("Ma'lumotlaringiz saqlab qolindi", chatId);
    }

    public User find(Long chatId){
        return userRepository.findUserByChatId(chatId);
    }
}
