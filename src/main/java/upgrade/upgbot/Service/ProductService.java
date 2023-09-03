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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final SendMessageService sendMessageService;

    private final CategoryRepository categoryRepository;

    private final CategoryService categoryService;

    private final LikedRepository likedRepository;

    public SendMessage getOneCategoryProduct(Integer categoryId, Long chatId, String category) {
        if (productRepository.findProductByCategoryId(categoryId).size() == 0) {
            return sendMessageService.sendMessage("ushbu katalog bo'yicha mahsulot topilmadi", chatId);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Product product : productRepository.findProductByCategoryId(categoryId)) {
            List<InlineKeyboardButton> rows = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(product.getName());
            inlineKeyboardButton.setCallbackData(String.valueOf(product.getId()));
            rows.add(inlineKeyboardButton);
            rowsInline.add(rows);
        }
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return sendMessageService.sendMessage(category + " katologi bo'yicha hamma mahsulotlar", chatId, inlineKeyboardMarkup);
    }

    public SendMessage getAll(Long chatId) {
        if (productRepository.findAll().size() == 0) {
            return sendMessageService.sendMessage("Mahsulot mavjud emas", chatId);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            List<InlineKeyboardButton> rows = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(product.getName());
            inlineKeyboardButton.setCallbackData(product.getId() + " choose");
            rows.add(inlineKeyboardButton);
            rowsInline.add(rows);
        }
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return sendMessageService.sendMessage("Hamma mahsulotlar", chatId, inlineKeyboardMarkup);
    }

    public SendMessage addProduct(Long chatId, String text, Map<Long, String> isProduct, Map<Long, String> name, Map<Long, String> price, Map<Long, String> description) {
        if (isProduct.get(chatId).equals("name")) {
            SendMessage sendMessage = sendMessageService.sendMessage("Endi esa narhini kiriting. So'mda ", chatId);
            isProduct.remove(chatId);
            name.put(chatId, text);
            isProduct.put(chatId, "price");
            return sendMessage;
        } else if (isProduct.get(chatId).equals("price")) {
            try {
                double v = Double.parseDouble(text);
                SendMessage sendMessage = sendMessageService.sendMessage("Mahsulot haqida ma'lumot kiriting", chatId);
                isProduct.remove(chatId);
                price.put(chatId, text);
                isProduct.put(chatId, "description");
                return sendMessage;
            } catch (NumberFormatException e) {
                return sendMessageService.sendMessage("narhni kiritishda hatolik. Narhni faqat raqamlarda kiriting", chatId);
            }
        } else if (isProduct.get(chatId).equals("description")) {
            SendMessage sendMessage = categoryService.getCategory(chatId, "Katalogni tanlang", "ni tanlang");
            isProduct.remove(chatId);
            description.put(chatId, text);
            isProduct.put(chatId, "category");
            return sendMessage;
        }
        return sendMessageService.sendMessage("hatolik", chatId);
    }

    public SendMessage insertProduct(Long chatId, String name, String description, String price, Integer categoryId, File photo) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("katalog topilmadi"));
        Product product = Product.builder()
                .price(Double.parseDouble(price))
                .img(photo)
                .category(category)
                .description(description)
                .build();
        product.setName(name);
        productRepository.save(product);
        return sendMessageService.sendMessage("mahsulot saqlandi", chatId);
    }

    public SendPhoto getOneProductForEdit(Long chatId, Product product) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Taxrirlash");
        inlineKeyboardButton.setCallbackData(product.getId() + " product edit");
        inlineKeyboardButton1.setText("Olib tashlash");
        inlineKeyboardButton1.setCallbackData(product.getId() + " product remove");
        button.add(inlineKeyboardButton);
        button.add(inlineKeyboardButton1);
        buttons.add(button);
        buttons.add(button1);
        inlineKeyboardMarkup.setKeyboard(buttons);
        InputFile inputFile = new InputFile(product.getImg().getFileId());
        return sendMessageService.sendPhoto(
                "▪️ Mahsulot nomi: " + product.getName() + "\n" +
                        "▪️ Mahsulot haqida: " + product.getDescription() + "\n" +
                        "▪️ Mahsulot narhi: " + product.getPrice() + " so'm \n", chatId, inputFile, inlineKeyboardMarkup
        );
    }

    public SendPhoto getOneProduct(Integer productId, Long chatId, String likeOrSimple) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("not found"));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button.setText(likeOrSimple.equals("simple") ? "❤️" : "💔");
        button.setCallbackData(likeOrSimple.equals("simple") ? "like " + product.getName() : "unlike" + product.getName());
        button1.setText("Buyurtma berish");
        button1.setCallbackData(product.getId() + "ordering");
        rowInline.add(button);
        rowInline.add(button1);
        rowsInline.add(rowInline);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        InputFile inputFile = new InputFile(product.getImg().getFileId());
        return sendMessageService.sendPhoto(
                "▪️ Mahsulot nomi: " + product.getName() + "\n" +
                        "▪️ Mahsulot haqida: " + product.getDescription() + "\n" +
                        "▪️ Mahsulot narhi: " + product.getPrice() + " so'm \n", chatId, inputFile, inlineKeyboardMarkup
        );
    }

    public AnswerCallbackQuery edit(String id, Integer productId, Integer categoryId, String name, String price, String description, File img) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("not found product"));
        if (name != null) {
            product.setName(name);
            productRepository.save(product);
            return sendMessageService.answerWithNotAlert(id, "Mahsulot nomi taxrirlandi ✅");
        } else if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("not found category"));
            product.setCategory(category);
            productRepository.save(product);
            return sendMessageService.answerWithNotAlert(id, "Mahsulot " + category.getName() + " katalogiga o'tqazildi  ✅");
        } else if (price != null) {
            product.setPrice(Double.valueOf(price));
            productRepository.save(product);
            return sendMessageService.answerWithNotAlert(id, "Mahsulot narhi taxrirlandi  ✅");
        } else if (description != null) {
            product.setDescription(description);
            productRepository.save(product);
            return sendMessageService.answerWithNotAlert(id, "Mahsulot ma'lumoti taxrirlandi  ✅");
        } else if (img != null) {
            product.setImg(img);
            productRepository.save(product);
            return sendMessageService.answerWithNotAlert(id, "Mahsulot rasmi taxrirlandi  ✅");
        }
        return sendMessageService.answerWithNotAlert(id, "Taxrirlashda hatolik");
    }

    public AnswerCallbackQuery remove(Integer productId, String callBackId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("not found product"));
        if (likedRepository.existsLikedByProductsId(productId)) {
            Liked liked = likedRepository.findLikedByProductsId(product.getId());
            liked.getProducts().remove(product);
            likedRepository.save(liked);
        }
        productRepository.delete(product);
        return sendMessageService.answer(callBackId, "Mahsulot olib tashlandi ✅✅");
    }


    public SendPhoto searchThis( Long chatId, Product product) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button.setText("❤️");
        button.setCallbackData("like" + product.getName());
        button1.setText("Buyurtma berish");
        button1.setCallbackData(product.getId() + "ordering");
        rowInline.add(button);
        rowInline.add(button1);
        rowsInline.add(rowInline);
        markup.setKeyboard(rowsInline);
        InputFile inputFile = new InputFile(product.getImg().getFileId());
        return sendMessageService.sendPhoto(
                "▪️ Mahsulot nomi: " + product.getName() + "\n" +
                        "▪️ Mahsulot haqida: " + product.getDescription() + "\n" +
                        "▪️ Mahsulot narhi: " + product.getPrice() + " so'm \n", chatId, inputFile, markup
        );
    }

    public List<SendPhoto> search(Long chatId, String name){
        List<SendPhoto> sendPhotos = new ArrayList<>();
        for (Product product : productRepository.findProductByName(name)) {
            SendPhoto sendPhoto = searchThis(chatId, product);
            sendPhotos.add(sendPhoto);
        }
        return sendPhotos;
    }
}
