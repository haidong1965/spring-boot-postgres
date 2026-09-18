package hd.webapp;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
@Transactional
public class FoodControllerTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	FoodRepository foodRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	JwtService jwtService;
	
	private String validToken;
	
	private User user1;
	
	@BeforeEach
    void setUp() {
		foodRepository.deleteAll();
		foodRepository.flush();
		foodRepository.save(new Food("food", 10, 10.0, 1.0, 10.0, 0));
		user1 = userService.createUser("test@yahoo.com", "USER", "1");
		validToken = jwtService.generateToken(user1.getName());
		
		System.out.println("✅ Food count in setUp: " + foodRepository.count());
	}
	
	@Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /food/list")
    void getAllFoods_asUser_returns200() throws Exception {
		User user1 = userService.createUser("mvctest@yahoo.com", "USER", "1");
		validToken = jwtService.generateToken(user1.getName());
        mockMvc.perform(get("/food/list")
            .header("Authorization", "Bearer " + validToken))
            .andDo(print())
            .andExpect(jsonPath("$", hasSize(1)))
           // .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("food"))
            .andExpect(status().isOk());
    }

}
