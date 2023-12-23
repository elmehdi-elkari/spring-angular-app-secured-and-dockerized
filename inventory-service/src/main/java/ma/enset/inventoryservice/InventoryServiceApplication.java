package ma.enset.inventoryservice;

import ma.enset.inventoryservice.entities.Product;
import ma.enset.inventoryservice.repositories.ProductsRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(ProductsRepo productsRepo) {
        return args->{
            productsRepo.save(
                    Product
                            .builder()
                            .id(UUID.randomUUID().toString())
                            .name("Computer")
                            .quantity(12)
                            .price(12000.0d)
                            .build()
            );
            productsRepo.save(
                    Product
                            .builder()
                            .id(UUID.randomUUID().toString())
                            .name("Phone")
                            .quantity(100)
                            .price(3200.0d)
                            .build()
            );
            productsRepo.save(
                    Product
                            .builder()
                            .id(UUID.randomUUID().toString())
                            .name("tab")
                            .quantity(50)
                            .price(5000.0d)
                            .build()
            );
        };
    }

}
