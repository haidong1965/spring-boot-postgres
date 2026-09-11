package hd.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

@Configuration
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager  transactionManager(EntityManagerFactory emf) {
       // DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
        JpaTransactionManager txManager = new JpaTransactionManager(emf);
        // ✅ ENABLE NESTED TRANSACTIONS
        txManager.setNestedTransactionAllowed(true); 
        
        return txManager;
    }
}