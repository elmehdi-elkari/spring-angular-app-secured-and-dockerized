package ma.enset.inventoryservice.web;

import ma.enset.inventoryservice.entities.Product;
import ma.enset.inventoryservice.repositories.ProductsRepo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductRestController {

    private ProductsRepo productsRepo;

    public ProductRestController(ProductsRepo productsRepo) {
        this.productsRepo = productsRepo;
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Product> products(){
        return productsRepo.findAll();
    }
}
