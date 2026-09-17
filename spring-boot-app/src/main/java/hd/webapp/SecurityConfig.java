package hd.webapp;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.jsonwebtoken.security.Keys;

@Configuration
public class SecurityConfig {

	@Value("${server.port}")
	private String serverPort;
	@Value("${client.port}")
	private String clientPort;
	@Value("${server.public}")
	private String serverPublic;
	@Value("${server.private}")
	private String serverPrivate;
	@Value("${jwt.secret}")
	private String jwtSecret;
	@Value("${management.server.address}")
	private String actuatorAddress;
	@Value("${management.server.port}")
	private String actuatorPort;
	
	private final JwtService jwtService;
	private final  CustomUserDetailsService customUserDetailsService;

	
	public SecurityConfig(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
		this.jwtService = jwtService;
	}
	
	@Bean AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


	@Bean CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(Arrays.asList(
				"https://" + serverPublic + ":" + serverPort, 
				"http://" + actuatorAddress + ":" + actuatorPort, // actuator specific
				"https://" + serverPublic + ":" + clientPort
		));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 1. Apply the CORS configuration first and most generally
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf
			         .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				     .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/users/list", "/delete/*", "/api/auth/activeUsers").hasRole("ADMIN")
						.requestMatchers("/signup", "/create", "/users/*", 
								"/api/auth/login", "/api/auth/logout", "/csrf", "/", 
								"/actuator/**",
								"/food/list", "/validate", "/user_food/*", "/user_food/**", "/file/*", "/file/**", "/audits", "/audits/**")
						.permitAll().requestMatchers("/api/public/**").permitAll().anyRequest().authenticated())
				.addFilterBefore(new JwtAuthFilter(jwtService, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);
;

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
