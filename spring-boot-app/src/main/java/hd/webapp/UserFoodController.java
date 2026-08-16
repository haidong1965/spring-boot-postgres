package hd.webapp;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user_food")
class UserFoodController {

	private final UserService userService;
	private final UserFoodService userFoodService;
	private final FoodService foodService;
	private final JwtService jwtService;

	private UserFoodController(UserFoodService userFoodService, UserService userService, FoodService foodService,
			JwtService jwtService) {
		this.userService = userService;
		this.foodService = foodService;
		this.userFoodService = userFoodService;
		this.jwtService = jwtService;
	}

	@PostMapping("/add")
	public ResponseEntity<Long> addUserFood(@Valid @RequestBody UserFoodRequest request) throws Exception {
		User user = userService.getUserByName(request.getUserName());
		Food food = foodService.getFoodByName(request.getFoodName());
		int unit = request.getUnit();
		int volumn = request.getVolumn();

		UserFood userFood = new UserFood(user.getId(), food.getId(), volumn, unit);
		userFoodService.addUserFood(userFood);
		return ResponseEntity.ok(userFood.getId());
	}

	@SuppressWarnings("deprecation")
	@GetMapping("/user/{requestedId}")
	private ResponseEntity<List<UserFood>> findByUserId(
			@RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long requestedId)
			throws Exception {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			User user = userService.getUserRepository().getById(requestedId);
			List<UserFood> userFoods = userFoodService.getUserFoodRepository().findByUserId(requestedId);
			if (jwtService.verifyToken(token))
				return ResponseEntity.ok(userFoods);
		}
		return null;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUserFood(@RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long id) {
		userFoodService.deleteById(id);
		return ResponseEntity.noContent().build(); // 204 No Content
	}
}
