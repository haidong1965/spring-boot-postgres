package hd.webapp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
class UserFoodControllerTest {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private final UserService userService;
	private final UserFoodService userFoodService;
	private final FoodService foodService;
	private final JwtService jwtService;

    private final MockMvc mockMvc;
    
    private String jwtToken;
    
    @Autowired 
    public UserFoodControllerTest(
    		UserFoodService userFoodService,
            UserService userService,
            FoodService foodService,
            MockMvc mockMvc,
          
            JwtService jwtService
    ) {
        this.userService = userService;
        this.foodService = foodService;
        this.userFoodService = userFoodService;
        this.mockMvc = mockMvc;
        this.jwtService = jwtService;
    }
     
     @Test
     void testAddUserFood() throws JsonProcessingException, Exception {
    	 User user1 = userService.createUser("mvctest@yahoo.com", "USER", "1");
    	 foodService.deleteAll();
    	 foodService.add(new Food("egg", 10, 10.0, 1.0, 10.0, 0));
 
    	 Food food1 = foodService.getFoodByName("egg");
    	 UserFood userFood1 = new UserFood(user1.getId(), food1.getId(), 200, 0);
    	 jwtToken = jwtService.generateToken(user1.getName());
    	 LocalDate today = LocalDate.now();
    	 
    	 UserFoodRequest userFoodRequest1 = new UserFoodRequest(user1.getName(), food1.getName(),  userFood1.getVolumn(), userFood1.getUnit());
    	 userFoodRequest1.setDate(today.toString());
         mockMvc.perform(post("/user_food/add")
        		 .header("Authorization", "Bearer " + jwtToken)
        		 .with(csrf())
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(userFoodRequest1)))
                 .andExpect(status().isOk());
         assertFalse(userFoodService.findAll().isEmpty());
         
         
         List<UserFood> userFoods = userFoodService.findAll();
         boolean existUser = false;

         for (UserFood userFood : userFoods) {
             
             if(userService.getUserReferenceById(userFood.getUserId()).getId() == user1.getId()) {
            	 existUser = true;
            	 break;
             }
         }
         assertTrue(existUser); 
     }
     
    
     
     @Test
     void testDeleteUserFood() throws JsonProcessingException, Exception {
    	 User user1 = userService.createUser("mvctest@yahoo.com", "USER", "1");
    	 foodService.deleteAll();
    	 foodService.add(new Food("egg", 10, 10.0, 1.0, 10.0, 0));
    	 Food food1 = foodService.getFoodByName("egg");
    	 UserFood uf = new UserFood(user1.getId(), food1.getId(), 1, 1);
    	 jwtToken = jwtService.generateToken(user1.getName());
    	 userFoodService.add(uf);
    	 mockMvc.perform(delete("/user_food/" + uf.getId())
    			 .header("Authorization", "Bearer " + jwtToken)
        		 .with(csrf()))
                 .andExpect(status().isNoContent()); 
     }
}
