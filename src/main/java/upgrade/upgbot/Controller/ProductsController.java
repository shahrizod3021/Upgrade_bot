package upgrade.upgbot.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upgrade.upgbot.Entity.Product;
import upgrade.upgbot.Repository.ProductRepository;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductsController {

    private final ProductRepository productRepository;

    @GetMapping
    public HttpEntity<?> getAll(){
        List<Product> all = productRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/name")
    public HttpEntity<?> getByName(@RequestParam(name = "name") String name){
        List<Product> productByName = productRepository.findProductByName(name);
        return ResponseEntity.ok(productByName);
    }
}
