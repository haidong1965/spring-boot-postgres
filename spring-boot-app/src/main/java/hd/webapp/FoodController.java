package hd.webapp;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/food")
public class FoodController {

	private final FoodService foodService;
	private final JwtService jwtService;
	
	private FoodController(FoodService foodService, JwtService jwtService) {
		this.foodService = foodService;
		this.jwtService = jwtService;
	}
	
	@GetMapping("/list")
	private  ResponseEntity<List<Food>> findAll(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
		List<Food> foods = foodService.getFoodRepository().findAll();
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			if (jwtService.verifyToken(token))
				return ResponseEntity.ok(foods);
		}
		return null;
	}
	
}
