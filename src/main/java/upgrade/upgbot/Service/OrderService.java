package upgrade.upgbot.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import upgrade.upgbot.Entity.Order;
import upgrade.upgbot.Entity.Product;
import upgrade.upgbot.Entity.User;
import upgrade.upgbot.Exception.ResourceNotFoundException;
import upgrade.upgbot.Repository.OrderRepository;
import upgrade.upgbot.Repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final SendMessageService sendMessageService;

    public AnswerCallbackQuery ordering(String id, Integer productId, User user){
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("not found product"));
        Order order = Order.builder().phoneNumber(user.getPhoneNumber()).product(product).build();
        order.setName(user.getName());
        orderRepository.save(order);
        return sendMessageService.answer(id, "Buyurtmangiz qabul qilindi✅✅. Siz bilan mumkin qadar tez orada bog'lanishadi");
    }
}
