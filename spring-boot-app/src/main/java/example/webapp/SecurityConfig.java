package example.webapp;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
	
	@Value("${server.port}")
	private String serverPort;
	@Value("${server.public}")
	private String serverPublic;
	@Value("${server.private}")
	private String serverPrivate;

	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// -----------------------------------------------
		// FIX: Build a single, permissive configuration object first.
		CorsConfiguration config = new CorsConfiguration();
		String corsAllowed = "https://" + serverPrivate + ":" + serverPort +" : "+ "https://" + serverPublic + ":" + serverPort;
		System.out.println("++++++++++++ CORSALLOWED: " + corsAllowed);
		config.setAllowedOrigins(Arrays.asList("https://" + serverPrivate + ":" + serverPort, "https://" + serverPublic + ":" + serverPort));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		// 🚀 Register the entire configuration object against ALL paths ("/**")
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfig)
			throws Exception {
		http
				// 1. Apply the CORS configuration first and most generally
				.cors(cors -> cors.configurationSource(corsConfig))
				// 2. Disable CSRF (Good for API testing)
				.csrf(csrf -> csrf
		                // CSRF is enabled by default
		                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				// 3. Authorize requests: This is where the fix lies.
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/signup", "/create", "/users/*", "/login", "/csrf", "/", "/food/list").permitAll()
						.requestMatchers("/api/public/**").permitAll().anyRequest().authenticated());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
