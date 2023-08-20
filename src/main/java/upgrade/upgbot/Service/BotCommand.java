package upgrade.upgbot.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import upgrade.upgbot.Repository.UserRepository;
import upgrade.upgbot.Utils.BotUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BotCommand {

    private final UserRepository userRepository;

    private final SendMessageService sendMessageService;


    public SendMessage start(Long chatId, Map<Long, String> isRegister) throws TelegramApiException {
        if (chatId.equals(BotUtils.ADMIN_CHATID)) {
            return sendMessageService.sendMessage("Tanlang", chatId, adminMenu());
        } else {
            if (!userRepository.existsUserByChatId(chatId)) {
                SendMessage salomIsmingizniKiriting = sendMessageService.sendMessage("salom ismingizni kiriting", chatId);
                isRegister.put(chatId, "name");
                return salomIsmingizniKiriting;
            } else {
                InlineKeyboardMarkup menu = menu();
                return sendMessageService.sendMessage("Bo'limni tanlang", chatId, menu);
            }
        }
    }

    public InlineKeyboardMarkup menu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline1 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button.setText("Kataloglar");
        button.setCallbackData("category");
        button1.setText("Mahsulot qidirish");
        button1.setCallbackData("search");
        button2.setText("Yoqtirilganlar");
        button2.setCallbackData("my liked");
        button3.setText("Bo'glanish");
        button3.setCallbackData("contact");
        rowinline.add(button);
        rowinline.add(button1);
        rowinline1.add(button2);
        rowinline1.add(button3);
        rowsinline.add(rowinline);
        rowsinline.add(rowinline1);
        markup.setKeyboard(rowsinline);
        return markup;
    }

    public InlineKeyboardMarkup adminMenu() {
        InlineKeyboardMarkup markupLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowinline2 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button.setText("Kataloglar");
        button.setCallbackData("category");
        button1.setText("Mahsulotlarni ko'rish");
        button1.setCallbackData("getProducts");
        button2.setText("Mahsulot qo'shish");
        button2.setCallbackData("addProduct");
        button3.setText("Katalog qo'yish");
        button3.setCallbackData("addCategory");
        button4.setText("Buyurtmalar");
        button4.setCallbackData("getOrders");
        rowinline.add(button);
        rowinline.add(button1);
        rowinline1.add(button2);
        rowinline1.add(button3);
        rowinline2.add(button4);
        rowsinline.add(rowinline);
        rowsinline.add(rowinline1);
        rowsinline.add(rowinline2);
        markupLine.setKeyboard(rowsinline);
        return markupLine;
    }
    public InlineKeyboardMarkup categoryAction(Integer id) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline1 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button.setText("Nomini taxrirlash");
        button.setCallbackData("edit name" + id);
        button1.setText("Rasmini taxrirlash");
        button1.setCallbackData("edit img" + id);
        button2.setText("Asosiy bo'lim ⬅️");
        button2.setCallbackData("start");
        rowinline.add(button);
        rowinline.add(button1);
        rowinline1.add(button2);
        rowsinline.add(rowinline);
        rowsinline.add(rowinline1);
        markup.setKeyboard(rowsinline);
        return markup;
    }

    public InlineKeyboardMarkup edit(Integer id) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline = new ArrayList<>();
        List<InlineKeyboardButton> rowinline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowinline2 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button.setText("Nomini taxrirlash");
        button.setCallbackData("edit product name" + id);
        button1.setText("Rasmini taxrirlash");
        button1.setCallbackData("edit product img" + id);
        button2.setText("Narhni taxrirlash");
        button2.setCallbackData("edit price" + id);
        button3.setText("Ma'lumotlarni taxrirlash");
        button3.setCallbackData("edit description" + id);
        button4.setText("Katalogni almashtirish");
        button4.setCallbackData("change category" + id);
        button5.setText("Asosiy bo'lim ⬅️");
        button5.setCallbackData("start");
        rowinline.add(button);
        rowinline.add(button1);
        rowinline1.add(button2);
        rowinline1.add(button3);
        rowinline2.add(button4);
        rowinline2.add(button5);
        rowsinline.add(rowinline);
        rowsinline.add(rowinline1);
        rowsinline.add(rowinline2);
        markup.setKeyboard(rowsinline);
        return markup;
    }


    public ReplyKeyboard phoneNumber() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Telefon raqam");
        keyboardButton.setRequestContact(true);
        row.add(keyboardButton);
        rows.add(row);
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
