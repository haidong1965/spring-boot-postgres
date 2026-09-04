package hd.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean ObjectMapper objectMapper() {
        // Creates the bean that Spring will now inject into your services
        return new ObjectMapper(); 
    }
}
