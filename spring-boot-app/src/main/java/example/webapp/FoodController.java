package example.webapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping("/food")
public class FoodController {

	private final FoodRepository foodRepository;
	private final JwtService jwtService;
	
	private FoodController(FoodRepository foodRepository, JwtService jwtService) {
		this.foodRepository = foodRepository;
		this.jwtService = jwtService;
	}
	
	@GetMapping("/list")
	private ResponseEntity<String> findAll(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
		System.out.println("++++++++++++++ FoodController: findAll() ++++++++++++++");
		List<Food> foods = foodRepository.findAll();
		String options = "";
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			System.out.println("++++++++++++++ FoodController token : " + token);
			if (jwtService.verifyToken(token)) {
				for(Food food : foods) {
					
					options = options + food.getName() + ":";
				}
				Gson gson = new Gson();
				System.out.println("++++++++++++++ get all foods ++++++++++++++" + options);
				
				return new ResponseEntity<>(options, HttpStatus.OK);
			}
		}
		return null;
	}
	
}
