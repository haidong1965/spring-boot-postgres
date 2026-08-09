package example.webapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; 
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    	String corsAllowed = "https://" + serverPrivate+ ":" + clientPort + " ----------- "+  "https://" + serverPrivate + ":"+ serverPort + " ----------- "+ "https://"  + serverPublic + ":" + serverPort;
		System.out.println("++++++++++++ CORSALLOWED: " + corsAllowed);
        registry.addMapping("/**") 
                .allowedOrigins("https://" + serverPrivate+ ":" + clientPort, "https://" + serverPrivate + ":"+ serverPort, "https://"  + serverPublic + ":" + serverPort)      // the spring boot end point ip address
        		.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}