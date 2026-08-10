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
	private  ResponseEntity<List<Food>> findAll(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
		System.out.println("++++++++++++++ FoodController: findAll() ++++++++++++++");
		List<Food> foods = foodRepository.findAll();
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			System.out.println("++++++++++++++ token : " + token);
		}
		if (token != null) {
			if (jwtService.verifyToken(token))
				return ResponseEntity.ok(foods);
		}
		return null;
	}
	
}
