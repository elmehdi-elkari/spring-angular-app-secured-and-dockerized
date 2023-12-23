package ma.enset.inventoryservice.repositories;

import ma.enset.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepo extends JpaRepository<Product,String> {
}
