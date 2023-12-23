package ma.enset.customer;

import ma.enset.customer.entities.Customer;
import ma.enset.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerFrontThymeleafAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerFrontThymeleafAppApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository){
        return args -> {
          customerRepository.save(Customer.builder().name("Rokia").email("rokia@gmail.com").build());
          customerRepository.save(Customer.builder().name("Ahmed").email("ahmed@gmail.com").build());
          customerRepository.save(Customer.builder().name("Youssef").email("youssef@gmail.com").build());

        };
    }
}
