package hd.webapp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.springframework.http.HttpStatus;
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
	
	public FoodController(FoodService foodService, JwtService jwtService) {
		this.foodService = foodService;
		this.jwtService = jwtService;
	}
	
	@GetMapping("/list")
	public  ResponseEntity<List<Food>> findAll(@RequestHeader(name = "Authorization", required = false) String authHeader) 
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		List<Food> foods = foodService.getAllFoodsSortedByName();
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if ((token != null) && jwtService.verifyToken(token))
				return ResponseEntity.ok(foods);
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
}
