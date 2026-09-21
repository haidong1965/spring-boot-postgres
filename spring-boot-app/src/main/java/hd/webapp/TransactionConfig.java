package hd.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class TransactionConfig {

    @Bean PlatformTransactionManager  transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager(emf);
        // ✅ ENABLE NESTED TRANSACTIONS
        txManager.setNestedTransactionAllowed(true); 
        
        return txManager;
    }
}