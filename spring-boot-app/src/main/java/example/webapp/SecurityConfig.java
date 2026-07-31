package example.webapp;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// -----------------------------------------------
		// FIX: Build a single, permissive configuration object first.
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("https://192.168.1.81:8443")); 
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		// If you need credentials/cookies to pass, uncomment this:
		config.setAllowCredentials(true);

		// 🚀 Register the entire configuration object against ALL paths ("/**")
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfig) throws Exception {
	    http
	        // 1. Apply the CORS configuration first and most generally
	        .cors(cors -> cors.configurationSource(corsConfig)) 	        
	        // 2. Disable CSRF (Good for API testing)
	        .csrf(csrf -> csrf.disable())         
	        // 3. Authorize requests: This is where the fix lies.
	        .authorizeHttpRequests(auth -> auth
	            // A. Permit ALL on /login for both browser AND curl
	        	.requestMatchers("/api/signup", "/create", "/users/list", "/my-new-page", "/api/login").permitAll()  
	        	// B. Permit all API routes that don't need specific role checks (e.g., public endpoints)
	            .requestMatchers("/api/public/**", "/users/login_test").permitAll() // <--- ADD any other known non-protected paths here
	            // C. Everything else requires authentication
	            .anyRequest().authenticated() 
	        );

	    return http.build();
	}

	/*
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Permit all requests without any authentication or authorization checks
        http
            .authorizeHttpRequests(request -> request
            		 .requestMatchers("/login").permitAll()
                     .requestMatchers("/users/**").hasRole("ADMIN")) 
            .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic Authentication (optional)
            .csrf(csrf -> csrf.disable()); // Disable CSRF protection (optional, depending on your use case)
        return http.build();
    }
    
	*/

	/**
	 * This method bean registers the BCryptEncoder with Spring, making it available
	 * to any service that requires it via constructor injection.
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
