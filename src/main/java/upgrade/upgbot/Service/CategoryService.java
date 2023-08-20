package upgrade.upgbot.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import upgrade.upgbot.Entity.Category;
import upgrade.upgbot.Entity.Liked;
import upgrade.upgbot.Entity.Product;
import upgrade.upgbot.Exception.ResourceNotFoundException;
import upgrade.upgbot.Repository.CategoryRepository;
import upgrade.upgbot.Repository.LikedRepository;
import upgrade.upgbot.Repository.ProductRepository;
import upgrade.upgbot.Utils.BotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final SendMessageService sendMessageService;

    private final ProductRepository productRepository;

    private final LikedRepository likedRepository;

    private final BotCommand botCommand;

    public SendPhoto getAllCategoryForAdmin(Long chatId, File file, String name, Integer id) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Taxrirlash");
        inlineKeyboardButton.setCallbackData(id + "ni taxrirlash");
        inlineKeyboardButton1.setText("Olib tashlash");
        inlineKeyboardButton1.setCallbackData(id + " olib tashlash");
        inlineKeyboardButton2.setText("Mahsulotlarini ko'rish");
        inlineKeyboardButton2.setCallbackData(id + "ni ko'rish");
        button.add(inlineKeyboardButton);
        button.add(inlineKeyboardButton1);
        button1.add(inlineKeyboardButton2);
        buttons.add(button);
        buttons.add(button1);
        inlineKeyboardMarkup.setKeyboard(buttons);
        InputFile inputFile = new InputFile(file.getFileId());
        return sendMessageService.sendPhoto(" " +
                        "   \n" +
                        "▪️" + name + " katalogi \n" +
                        "    ",
                chatId, inputFile, inlineKeyboardMarkup);
    }
    public SendPhoto getAllCategoryForUser(Long chatId, File file, String name, Integer id) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Mahsulotlarini ko'rish");
        inlineKeyboardButton.setCallbackData(id + "ni ko'rish");
        button.add(inlineKeyboardButton);
        buttons.add(button);
        inlineKeyboardMarkup.setKeyboard(buttons);
        InputFile inputFile = new InputFile(file.getFileId());
        return sendMessageService.sendPhoto(" " +
                        "   \n" +
                        "▪️" + name + " katalogi \n" +
                        "    ",
                chatId, inputFile, inlineKeyboardMarkup);
    }


    public SendMessage getCategory(Long chatId, String text, String changeOrChoose) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            List<InlineKeyboardButton> button = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(category.getName());
            inlineKeyboardButton.setCallbackData(category.getId() + changeOrChoose);
            button.add(inlineKeyboardButton);
            buttons.add(button);
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return sendMessageService.sendMessage(text, chatId, inlineKeyboardMarkup);
    }


    public List<SendPhoto> getCategoryButtonWithPhoto(Long chatId) {
        List<SendPhoto> sendPhotos = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            SendPhoto allCategory;
            if (chatId.equals(BotUtils.ADMIN_CHATID)){
                allCategory = getAllCategoryForAdmin(chatId, category.getImg(), category.getName(), category.getId());
            }else {
                allCategory = getAllCategoryForUser(chatId, category.getImg(), category.getName(), category.getId());
            }
            sendPhotos.add(allCategory);
        }
        return sendPhotos;
    }

    public SendMessage addCategory(Long chatId, String text, Map<Long, String> isCategory, Map<Long, String> isName) {
        if (isCategory.get(chatId).equals("name")) {
            SendMessage sendMessage = sendMessageService.sendMessage("endi rasmni kiriting", chatId);
            isCategory.remove(chatId);
            isName.put(chatId, text);
            isCategory.put(chatId, "img");
            return sendMessage;
        }
        return sendMessageService.sendMessage("uzur dasturda hatolik bor", chatId);
    }

    public void insertCategory(String name, File photo) {
        Category build = Category.builder()
                .img(photo)
                .build();
        build.setName(name);
        categoryRepository.save(build);
    }

    public SendMessage edit(String name, File img, Integer categoryId, Long chatId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Categoriya topilmadi"));
        if (name != null) {
            category.setName(name);
            categoryRepository.save(category);
            return sendMessageService.sendMessage("nomi taxrirlandi", chatId, botCommand.categoryAction(categoryId));
        } else {
            category.setImg(img);
            categoryRepository.save(category);
            return sendMessageService.sendMessage("rasmi taxrirlandi", chatId, botCommand.categoryAction(categoryId));
        }
    }

    public AnswerCallbackQuery remove(Integer categoryId, String id) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("not found"));
        List<Integer> ids = new ArrayList<>();
        for (Product product : productRepository.findProductByCategoryId(categoryId)) {
            ids.add(product.getId());
            if (likedRepository.existsLikedByProductsId(product.getId())){
                Liked liked = likedRepository.findLikedByProductsId(product.getId());
                liked.setProducts(null);
                likedRepository.save(liked);
            }
        }
        productRepository.deleteAllById(ids);
        categoryRepository.delete(category);
        return sendMessageService.answer(id, category.getName() + " katalogi olib tashlandi. ❗❗ \nEslab qoling Katalog o'chirildi va uning ichidagi mahsulotlar ham o'chib ketadi");
    }


}
