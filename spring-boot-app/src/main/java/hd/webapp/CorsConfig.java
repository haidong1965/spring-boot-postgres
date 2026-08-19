package hd.webapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; 
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**  TODO: seems this is class useless now */

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	
	@Value("${server.port}")
	private String serverPort;
	@Value("${client.port}")
	private String clientPort;
	@Value("${server.public}")
	private String serverPublic;
	@Value("${server.private}")
	private String serverPrivate;

    @Override
    public void addCorsMappings(CorsRegistry registry) { 
    	registry.addMapping("/**") 
                .allowedOrigins(
                		"https://" + serverPublic + ":" + serverPort, 
        				"https://" + serverPublic + ":" + clientPort)     
        		.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}