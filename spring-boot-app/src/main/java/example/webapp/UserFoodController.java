package example.webapp;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user_food")
class UserFoodController {

	private UserFoodController(UserFoodRepository userFoodRepository, 
	    UserService userService,
        FoodService foodService,  
		JwtService jwtService) {
	}

	

	

}
