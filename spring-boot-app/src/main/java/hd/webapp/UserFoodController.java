package hd.webapp;

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

	private UserFoodController(UserFoodService userFoodService, 
	    UserService userService,
        FoodService foodService,  
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
        
        UserFood userFood = new UserFood(user.getId(), food.getId(), unit, volumn);
        userFoodService.addUserFood(userFood);
		return ResponseEntity.ok(userFood.getId());
	}

}
