package upgrade.upgbot.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import upgrade.upgbot.Entity.Liked;
import upgrade.upgbot.Entity.Product;
import upgrade.upgbot.Repository.LikedRepository;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

@Service
@RequiredArgsConstructor
public class LikedService {

    private final LikedRepository likedRepository;

    private final SendMessageService sendMessageService;
    public boolean foundOnLikedList(Product product, Long chatId){
        Liked liked = likedRepository.findLikedByUsersChatId(chatId);
        if (!liked.getProducts().isEmpty()) {
            for (Product likedProduct : liked.getProducts()) {
                if (likedProduct.getId().equals(product.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public AnswerCallbackQuery insertToLiked(Product product, Long chatId, String id){
        boolean b = foundOnLikedList(product, chatId);
        if (b){
            return sendMessageService.answerWithNotAlert(id,"ushbu mahsulot yoqtirilganlar ro'yhatida mavjud");
        }else {
            Liked liked = likedRepository.findLikedByUsersChatId(chatId);
            liked.getProducts().add(product);
            likedRepository.save(liked);
            return sendMessageService.answerWithNotAlert(id,"Yoqtirilganlarga saqlandi");
        }
    }

    public SendMessage getMyLiked(Long chatId){
        if (likedRepository.findLikedByUsersChatId(chatId).getProducts().size()  == 0){
            return sendMessageService.sendMessage("Yoqtirilganlar ro'yhatida hech narsa yo'q", chatId);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Product product : likedRepository.findLikedByUsersChatId(chatId).getProducts()) {
            List<InlineKeyboardButton> rows = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(product.getName());
            inlineKeyboardButton.setCallbackData(product.getId() + "like this");
            rows.add(inlineKeyboardButton);
            rowsInline.add(rows);
        }
        markup.setKeyboard(rowsInline);
        return sendMessageService.sendMessage("Mening yoqtirganlarim", chatId, markup);
    }
}
