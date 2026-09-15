package hd.webapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class SpringBootAppApplicationTests {
	
    @Autowired private FoodService foodService;
    @Autowired private UserService userService;
    @Autowired private UserFoodService userFoodService;
    @Autowired private EmailService emailService;
    @Autowired private JwtService jwtService;
    @Autowired private FoodRepository foodRepository;


	@Test
	void contextLoads(ApplicationContext context) {
		assertThat(context).isNotNull();
	}

	@Test
	void verifyPasswordEncoder(ApplicationContext context) {
	    assertTrue(context.containsBean("passwordEncoder"), 
            "Bean named 'passwordEncoder' should exist");

        // 4. Retrieve the bean and verify it is specifically a BCryptPasswordEncoder
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        
        assertNotNull(encoder, "PasswordEncoder bean should not be null");
        assertTrue(encoder instanceof BCryptPasswordEncoder, 
            "PasswordEncoder should be an instance of BCryptPasswordEncoder");
	}
	
	@Test
    void allRequiredBeansArePresent() {
        assertThat(foodService).isNotNull();
        assertThat(jwtService).isNotNull();
        assertThat(foodRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(emailService).isNotNull();
        assertThat(userFoodService).isNotNull();
    }
}
