package hd.webapp;

import java.util.ArrayList;
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

	public UserFoodController(UserFoodService userFoodService, UserService userService, FoodService foodService,
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
		userFood.setDate(request.getDate());
		userFoodService.addUserFood(userFood);
		return ResponseEntity.ok(userFood.getId());
	}

	@GetMapping("/user/{requestedId}")
	public ResponseEntity<List<UserFoodDTO>> findByUserId(
			@RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long requestedId)
			throws Exception {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			List<UserFood> userFoods = userFoodService.getUserFoodRepository().findByUserId(requestedId);
			List<UserFoodDTO> userFoodsDTO = new ArrayList<UserFoodDTO>();
			for(UserFood uf : userFoods) {
				double weight = 1.0;
				if(uf.getUnit() == 0)
					weight = 100.0;
				Long id = uf.getId();
				Food foodReference = foodService.getFoodReferenceById(uf.getFoodId());
				UserFoodDTO userFoodDTO = new UserFoodDTO(
						id,
						uf.getUserId(),
						foodReference.getName(),
						(int)(foodReference.getEnergy()*uf.getVolumn()/weight),
						foodReference.getProtein()*uf.getVolumn()/weight,
						foodReference.getFiber()*uf.getVolumn()/weight,
						foodReference.getCalcium()*uf.getVolumn()/weight,
						uf.getDate(),
						uf.getUnit(),
						uf.getVolumn()
						);
				userFoodsDTO.add(userFoodDTO);
			}
			if (jwtService.verifyToken(token))
				return ResponseEntity.ok(userFoodsDTO);
		}
		return null;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUserFood(@RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long id) {
		userFoodService.deleteById(id);
		return ResponseEntity.noContent().build(); // 204 No Content
	}
}
