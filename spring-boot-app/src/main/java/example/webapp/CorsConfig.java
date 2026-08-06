package example.webapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; 
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) { 
        registry.addMapping("/**") 
                .allowedOrigins("https://192.168.1.81:5174", "https://192.168.1.81:8444", "https://2.106.164.55:8444")      // the spring boot end point ip address
        		.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}