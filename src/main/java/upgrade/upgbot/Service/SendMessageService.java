package upgrade.upgbot.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Service
public class SendMessageService {

    public SendMessage sendMessage(String message, Long chatId){
        return SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();
    }

    public SendMessage sendMessage(String message, Long chatId, ReplyKeyboard replyKeyboard){
        return  SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .replyMarkup(replyKeyboard)
                .build();
    }

    public SendPhoto sendPhoto(String caption, Long chatId, InputFile inputFile, ReplyKeyboard replyKeyboard){
        return SendPhoto.builder()
                .caption(caption)
                .chatId(chatId)
                .photo(inputFile)
                .replyMarkup(replyKeyboard)
                .build();
    }

    public SendPhoto sendPhoto(String caption, Long chatId, InputFile inputFile){
        return SendPhoto.builder()
                .photo(inputFile)
                .caption(caption)
                .chatId(chatId)
                .build();
    }

    public AnswerCallbackQuery answer(String callBackId, String text){
        return AnswerCallbackQuery.builder()
                .text(text)
                .callbackQueryId(callBackId)
                .showAlert(true)
                .build();
    }

    public AnswerCallbackQuery answerWithNotAlert(String callBackId, String text){
        return AnswerCallbackQuery.builder()
                .text(text)
                .callbackQueryId(callBackId)
                .showAlert(false)
                .build();
    }

}
