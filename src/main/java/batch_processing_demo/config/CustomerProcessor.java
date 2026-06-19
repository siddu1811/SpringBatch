package batch_processing_demo.config;

import batch_processing_demo.entity.Customer;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDateTime;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) throws Exception {

        item.setCreatedAt(LocalDateTime.now());

        System.out.println(
                Thread.currentThread().getName()
                        + " Processing Customer Id = "
                        + item.getId());

        return item;
    }
}

