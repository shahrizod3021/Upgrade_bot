package upgrade.upgbot.Bot;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import upgrade.upgbot.Config.BotConfig;
import upgrade.upgbot.Entity.Category;
import upgrade.upgbot.Entity.Product;
import upgrade.upgbot.Repository.CategoryRepository;
import upgrade.upgbot.Repository.ProductRepository;
import upgrade.upgbot.Service.*;
import upgrade.upgbot.Utils.BotUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private final SendMessageService sendMessageService;

    private final BotCommand botCommand;

    private final CategoryRepository categoryRepository;

    private final UserService userService;

    private final CategoryService categoryService;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final BotConfig botConfig;

    private final LikedService likedService;

    private final OrderService orderService;
    Map<Long, String> phoneNumber = new HashMap<>();
    Map<Long, String> isRegister = new HashMap<>();
    Map<Long, String> name = new HashMap<>();
    Map<Long, String> isCategory = new HashMap<>();

    Map<Long, String> search = new HashMap<>();
    Map<Long, String> categoryName = new HashMap<>();
    Map<Long, File> categoryPhoto = new HashMap<>();
    Map<Long, String> isProduct = new HashMap<>();
    Map<Long, String> productName = new HashMap<>();
    Map<Long, String> productPrice = new HashMap<>();
    Map<Long, String> productDescription = new HashMap<>();
    Map<Long, Integer> categoryId = new HashMap<>();
    Map<Long, Integer> productId = new HashMap<>();
    Map<Long, String> callBackId = new HashMap<>();
    Map<Long, File> productPhoto = new HashMap<>();
    Map<Long, String> editName = new HashMap<>();
    Map<Long, String> editImg = new HashMap<>();
    Map<Long, String> editPrice = new HashMap<>();

    Map<Long, String> editDescription = new HashMap<>();
    Map<Long, String> editCategory = new HashMap<>();


    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);


    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = update.getMessage();
        if (update.hasMessage()) {
            String text = message.getText();
            Long chatId = message.getChatId();
            List<PhotoSize> photo = message.getPhoto();
            PhotoSize photoSize = new PhotoSize();
            LOGGER.info("user chatId: ➡️ " + chatId);
            if (message.hasText()) {
                if (text.equals("/start")) {
                    SendMessage start = botCommand.start(chatId, isRegister);
                    execute(start);
                }else if (text.equals("/getmyliked")){
                    execute(likedService.getMyLiked(chatId));
                }else if (text.equals("/getcategory")){
                    if (!categoryRepository.findAll().isEmpty()) {
                        for (SendPhoto sendPhoto : categoryService.getCategoryButtonWithPhoto(chatId)) {
                            execute(sendPhoto);
                        }
                    } else {
                        execute(sendMessageService.sendMessage( "Kataloglar mavjud emas", chatId, chatId.equals(BotUtils.ADMIN_CHATID) ? botCommand.adminMenu() : botCommand.menu()));
                    }
                }
                else if (isRegister.size() > 0) {
                    SendMessage register = register(text, chatId);
                    execute(register);
                } else if (isCategory.size() > 0) {
                    execute(categoryService.addCategory(chatId, text, isCategory, categoryName));
                } else if (isProduct.size() > 0) {
                    execute(productService.addProduct(chatId, text, isProduct, productName, productPrice, productDescription));
                } else if (editName.size() > 0) {
                    execute(edit(chatId, text, categoryId.get(chatId)));
                    execute(edit(chatId, text, productId.get(chatId), callBackId.get(chatId)));
                } else if (editPrice.size() > 0) {
                    execute(edit(chatId, text, productId.get(chatId), callBackId.get(chatId)));
                } else if (editDescription.size() > 0) {
                    execute(edit(chatId, text, productId.get(chatId), callBackId.get(chatId)));
                }else if (search.size() > 0){
                    search(chatId, text);
                }
            } else if (message.hasContact()) {
                if (isRegister.get(chatId).equals("phone")) {
                    Contact contact = message.getContact();
                    phoneNumber.put(chatId, contact.getPhoneNumber());
                    execute(userService.addUser(chatId, name.get(chatId), phoneNumber.get(chatId)));
                    execute(sendMessageService.sendMessage("O'zingizga ma'qul bo'lgan menuni tanlang", chatId, botCommand.menu()));
                    isRegister.clear();
                }
            } else if (message.hasPhoto()) {
                if (isCategory.size() > 0 && isCategory.get(chatId).equals("img")) {
                    execute(sendMessageService.sendMessage("Categoriya saqlandi endi uni bemalol ko'rishingiz mumkin", chatId, botCommand.adminMenu()));
                    photo.add(new PhotoSize(photoSize.getFileId(), photoSize.getFileUniqueId(), photoSize.getWidth(), photoSize.getHeight(), photoSize.getFileSize(), photoSize.getFilePath()));
                    for (PhotoSize size : photo) {
                        File file = new File(size.getFileId(), size.getFileUniqueId(), size.getFileSize().longValue(), size.getFilePath());
                        categoryPhoto.put(chatId, file);
                        categoryService.insertCategory(categoryName.get(chatId), categoryPhoto.get(chatId));
                        isCategory.remove(chatId);
                        LOGGER.warn("Is Category size ➡️" + isCategory.size());
                        break;
                    }
                } else if (isProduct.size() > 0 && isProduct.get(chatId).equals("productPhoto")) {
                    execute(sendMessageService.sendMessage("bo'limni tanlang", chatId, botCommand.adminMenu()));
                    photo.add(new PhotoSize(photoSize.getFileId(), photoSize.getFileUniqueId(), photoSize.getWidth(), photoSize.getHeight(), photoSize.getFileSize(), photoSize.getFilePath()));
                    for (PhotoSize size : photo) {
                        File file = new File(size.getFileId(), size.getFileUniqueId(), size.getFileSize().longValue(), size.getFilePath());
                        productPhoto.put(chatId, file);
                        execute(productService.insertProduct(chatId, productName.get(chatId), productDescription.get(chatId), productPrice.get(chatId), categoryId.get(chatId), productPhoto.get(chatId)));
                        isProduct.remove(chatId);
                        break;
                    }
                } else if (editImg.get(chatId).equals("img")) {
                    photo.add(new PhotoSize(photoSize.getFileId(), photoSize.getFileUniqueId(), photoSize.getWidth(), photoSize.getHeight(), photoSize.getFileSize(), photoSize.getFilePath()));
                    for (PhotoSize size : photo) {
                        File file = new File(size.getFileId(), size.getFileUniqueId(), size.getFileSize().longValue(), size.getFilePath());
                        productPhoto.put(chatId, file);
                        execute(categoryService.edit(null, productPhoto.get(chatId), categoryId.get(chatId), chatId));
                        break;
                    }
                } else if (editImg.get(chatId).equals("change img")) {
                    photo.add(new PhotoSize(photoSize.getFileId(), photoSize.getFileUniqueId(), photoSize.getWidth(), photoSize.getHeight(), photoSize.getFileSize(), photoSize.getFilePath()));
                    for (PhotoSize size : photo) {
                        File file = new File(size.getFileId(), size.getFileUniqueId(), size.getFileSize().longValue(), size.getFilePath());
                        productPhoto.put(chatId, file);
                        execute(productService.edit(callBackId.get(chatId), productId.get(chatId), null, null, null, null, productPhoto.get(chatId)));
                        execute(sendMessageService.sendMessage("Taxrirlash uchun tanlang", chatId, botCommand.edit(productId.get(chatId))));
                        break;
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            String data = callbackQuery.getData();
            Long chatId = callbackQuery.getMessage().getChatId();
            String id = callbackQuery.getId();
            if (data.equals("category")) {
                if (!categoryRepository.findAll().isEmpty()) {
                    for (SendPhoto sendPhoto : categoryService.getCategoryButtonWithPhoto(chatId)) {
                        execute(sendPhoto);
                    }
                } else {
                    execute(sendMessageService.answer(id, "Kataloglar mavjud emas"));
                }
            } else if (data.equals("addCategory")) {
                execute(sendMessageService.sendMessage("katalog nomini kiriting", chatId));
                isCategory.put(chatId, "name");
            } else if (data.equals("addProduct")) {
                execute(sendMessageService.sendMessage("Mahsulot nomini kiriting", chatId));
                isCategory.remove(chatId);
                LOGGER.error("Is Category Size ➡️  " + isCategory.size());
                isProduct.put(chatId, "name");
            } else if (data.equals("start")) {
                execute(botCommand.start(chatId, isRegister));
            } else if (data.equals("getProducts")) {
                execute(productService.getAll(chatId));
            } else if (data.equals("my liked")) {
                execute(likedService.getMyLiked(chatId));
            }else if (data.equals("search")){
                execute(sendMessageService.sendMessage("Mahsulot nomini kiriting", chatId));
                search.put(chatId, "name");
            }else if (data.equals("getOrders")){

            }
            for (Product product : productRepository.findAll()) {
                if (data.equals(String.valueOf(product.getId()))) {
                    execute(productService.getOneProduct(product.getId(), chatId, "simple"));
                    break;
                } else if (data.equals("like " + product.getName())) {
                    execute(likedService.insertToLiked(product, chatId, id));
                    break;
                } else if (data.equals(product.getId() + " choose")) {
                    execute(productService.getOneProductForEdit(chatId, product));
                    break;
                } else if (data.equals(product.getId() + " product edit")) {
                    execute(sendMessageService.sendMessage("Mahsulotning nimasini taxrirlaysiz", chatId, botCommand.edit(product.getId())));
                    break;
                } else if (data.equals(product.getId() + " product remove")) {
                    execute(productService.remove(product.getId(), id));
                    break;
                } else if (data.equals("edit product name" + product.getId())) {
                    execute(sendMessageService.sendMessage("Yangi nomini kiriting", chatId));
                    editName.put(chatId, "product name");
                    callBackId.put(chatId, id);
                    productId.put(chatId, product.getId());
                    break;
                } else if (data.equals("edit price" + product.getId())) {
                    execute(sendMessageService.sendMessage("Yangi narxni kiriting", chatId));
                    editPrice.put(chatId, "product price");
                    callBackId.put(chatId, id);
                    productId.put(chatId, product.getId());
                    break;
                } else if (data.equals("edit description" + product.getId())) {
                    execute(sendMessageService.sendMessage("Yangi ma'lumotlarni kiritng", chatId));
                    editDescription.put(chatId, "product description");
                    callBackId.put(chatId, id);
                    productId.put(chatId, product.getId());
                    break;
                } else if (data.equals("change category" + product.getId())) {
                    execute(categoryService.getCategory(chatId, "Mahsulot katalogini almashtirish uchun tanlang", "change category"));
                    editCategory.put(chatId, "product of category");
                    callBackId.put(chatId, id);
                    productId.put(chatId, product.getId());
                    break;
                } else if (data.equals("edit product img" + product.getId())) {
                    execute(sendMessageService.sendMessage("Mahsulot uchun yangi rasm kiriting", chatId));
                    editImg.put(chatId, "change img");
                    callBackId.put(chatId, id);
                    productId.put(chatId, product.getId());
                    break;
                } else if (data.equals(product.getId() + "like this")) {
                    execute(productService.getOneProduct(product.getId(), chatId, "like"));
                    break;
                } else if (data.equals(product.getId() + "ordering")) {
                    execute(orderService.ordering(id, product.getId(), userService.find(chatId)));
                    execute(sendMessageService.sendMessage("Sizda yangi buyurtma mavjud", 1681505177L));
                    break;
                }
            }
            for (Category category : categoryRepository.findAll()) {
                if (data.equals(category.getId() + "ni ko'rish")) {
                    execute(productService.getOneCategoryProduct(category.getId(), chatId, category.getName()));
                    break;
                } else if (data.equals(category.getId() + "ni taxrirlash")) {
                    execute(sendMessageService.sendMessage("Katalogning nimasini taxrirlaysiz", chatId, botCommand.categoryAction(category.getId())));
                    break;
                } else if (data.equals(category.getId() + " olib tashlash")) {
                    execute(categoryService.remove(category.getId(), id));
                } else if (data.equals("edit name" + category.getId())) {
                    execute(sendMessageService.sendMessage("Taxrirlash uchun yangi nomni kiriting", chatId));
                    editName.put(chatId, "name");
                    categoryId.put(chatId, category.getId());
                    break;
                } else if (data.equals("edit img" + category.getId())) {
                    execute(sendMessageService.sendMessage("Taxrirlash uchun yangi rasmni kiriting", chatId));
                    editImg.put(chatId, "img");
                    categoryId.put(chatId, category.getId());
                    break;
                } else if (data.equals(category.getId() + "ni tanlang")) {
                    if (isProduct.size() > 0 && isProduct.get(chatId).equals("category")) {
                        execute(sendMessageService.answerWithNotAlert(id, "Katalog tanlandi. Endi rasm kiriting ❗❗"));
                        isProduct.remove(chatId);
                        categoryId.put(chatId, category.getId());
                        isProduct.put(chatId, "productPhoto");
                    }
                    break;
                } else if (data.equals(category.getId() + "change category")) {
                    if (editCategory.get(chatId).equals("product of category")) {
                        editCategory.remove(chatId);
                        execute(productService.edit(id, productId.get(chatId), category.getId(), null, null, null, null));
                        execute(sendMessageService.sendMessage("Taxrirlash uchun tanlang", chatId, botCommand.edit(productId.get(chatId))));
                    }
                    break;
                }
            }
        }
    }

    public SendMessage edit(Long chatId, String text, Integer categoryId) {
        if (editName.size() > 0 && editName.get(chatId).equals("name")) {
            editName.remove(chatId);
            name.put(chatId, text);
            return categoryService.edit(name.get(chatId), null, categoryId, chatId);
        }
        return SendMessage.builder().chatId(chatId).text(".").build();
    }

    public AnswerCallbackQuery edit(Long chatId, String text, Integer productId, String callBackId) {
        if (editName.size() > 0 && editName.get(chatId).equals("product name")) {
            editName.remove(chatId);
            productName.put(chatId, text);
            return productService.edit(callBackId, productId, null, productName.get(chatId), null, null, null);
        } else if (editPrice.size() > 0 && editPrice.get(chatId).equals("product price")) {
            try {
                double v = Double.parseDouble(text);
                editPrice.remove(chatId);
                productPrice.put(chatId, text);
                return productService.edit(callBackId, productId, null, null, productPrice.get(chatId), null, null);
            } catch (NumberFormatException e) {
                return sendMessageService.answer(callBackId, "Narh faqatgina narhlarda kiritlsin. Narhlar o'rtasida bo'sh joy qoldirilmasin❗❗❗");
            }
        } else if (editDescription.size() > 0 && editDescription.get(chatId).equals("product description")) {
            editDescription.remove(chatId);
            productDescription.put(chatId, text);
            return productService.edit(callBackId, productId, null, null, null, productDescription.get(chatId), null);
        }
        return AnswerCallbackQuery.builder().text("bajarilmoqda...").callbackQueryId(callBackId).build();
    }

    @SneakyThrows
    public void search(Long chatId, String text){
        if (search.get(chatId).equals("name")){
            if (productRepository.findProductByName(text).size() > 0){
                for (SendPhoto sendPhoto : productService.search(chatId, text)) {
                    execute(sendPhoto);
                }
                search.remove(chatId);
            }else {
                execute(sendMessageService.sendMessage("Qidiruv natijasida hech narsa topilmadi🫤☹️", chatId));
            }
        }
    }
    public SendMessage register(String text, Long chatId) {
        if (isRegister.get(chatId).equals("name")) {
            SendMessage endiTelefonRaqamniKiriting = sendMessageService.sendMessage("endi telefon raqamni kiriting", chatId, botCommand.phoneNumber());
            isRegister.remove(chatId);
            name.put(chatId, text);
            isRegister.put(chatId, "phone");
            return endiTelefonRaqamniKiriting;
        }
        return null;
    }


}

