package hd.webapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
@Transactional
public class UserFoodControllerTest {
	
	private final UserFoodRepository userFoodRepository;
	private final UserService userService;
	private final FoodService foodService;
	@Autowired
	FoodRepository foodRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public UserFoodControllerTest(
            UserFoodRepository userFoodRepository,
            UserService userService,
            FoodService foodService,
            MockMvc mockMvc,
            JwtService jwtService
    ) {
        this.userFoodRepository = userFoodRepository;
        this.userService = userService;
        this.foodService = foodService;
        this.mockMvc = mockMvc;
    }
     
     @Test
     void testAddUserFood() throws JsonProcessingException, Exception {
    	 User user1 = userService.createUser("mvctest@yahoo.com", "USER", "1");
    	 foodRepository.deleteAll();
 		 foodRepository.flush();
 		 foodRepository.save(new Food("egg", 10, 10.0, 1.0, 10.0, 0));
    	 Food food1 = foodService.getFoodByName("egg");
    	 UserFood userFood1 = new UserFood(user1.getId(), food1.getId(), 200, 0);
    	 
    	 LocalDate today = LocalDate.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         String formattedDate = today.format(formatter);
    	 
    	 UserFoodRequest userFoodRequest1 = new UserFoodRequest(user1.getName(), food1.getName(),  userFood1.getVolumn(), userFood1.getUnit());
    	 userFoodRequest1.setDate(today.toString());
         mockMvc.perform(post("/user_food/add")
        		 .with(csrf())
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(userFoodRequest1)))
                 .andExpect(status().isOk());
         assertThat(!userFoodRepository.findAll().isEmpty());
         
         
         List<UserFood> userFoods = userFoodRepository.findAll();
         boolean existUser = false;

         for (UserFood userFood : userFoods) {
             System.out.println(
                 "foodId=" + userFood.getFoodId() +
                 ", userId=" + userFood.getUserId() + ", date=" + userFood.getDate()
             );
             if(userService.getUserReferenceById(userFood.getUserId()).getId() == user1.getId()) {
            	 existUser = true;
            	 break;
             }
         }
         assertTrue(existUser); 
     }
     
     /*
     @Test
     void testFindByUserId() throws JsonProcessingException, Exception {
    	 User user1 = userService.getUserByName("mvctest@yahoo.com");
    	 Food food1 = foodService.getFoodByName("egg");
    	 UserFood userFood1 = new UserFood(user1.getId(), food1.getId(), 200, 0);
    	 String token = jwtService.generateToken(user1.getName());
    	 UserFoodRequest userFoodRequest1 = new UserFoodRequest(user1.getName(), food1.getName(),  userFood1.getVolumn(), userFood1.getUnit());
         mockMvc.perform(get("/user_food/" + user1.getId())
        		 .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        		 .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());
    	 
     }
     */
     
     @Test
     void testDeleteUserFood() throws JsonProcessingException, Exception {
    	 User user1 = userService.createUser("mvctest@yahoo.com", "USER", "1");
    	 foodRepository.deleteAll();
 		 foodRepository.flush();
 		 foodRepository.save(new Food("egg", 10, 10.0, 1.0, 10.0, 0));
    	 Food food1 = foodService.getFoodByName("egg");
    	 UserFood uf = new UserFood(user1.getId(), food1.getId(), 1, 1);

    	 userFoodRepository.save(uf);
    	 mockMvc.perform(delete("/user_food/" + uf.getId())
        		 .with(csrf()))
                 .andExpect(status().isNoContent()); 
     }
}
