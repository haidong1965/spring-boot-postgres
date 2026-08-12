package example.webapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user_food")
class UserFoodController {

	private UserFoodController(UserFoodRepository userFoodRepository, 
	    UserService userService,
        FoodService foodService,  
		JwtService jwtService) {
	}

	@GetMapping("/add")
	public ResponseEntity<String> addUserFood(@RequestParam("user_name") String name,  @RequestParam("token") String token) throws Exception {
		
		
		String response = "<html><body>Congratulations " + name + "! <br/>You can go to the login page to login  now<br/> </body></html>";
		return ResponseEntity.ok(response);
	}


	

}
