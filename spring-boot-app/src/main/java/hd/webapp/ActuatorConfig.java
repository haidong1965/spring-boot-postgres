package hd.webapp;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        // Stores up to 100 exchanges by default
        return new InMemoryHttpExchangeRepository();
    }
}
