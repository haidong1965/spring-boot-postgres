package example.webapp;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFoodService {

	private final UserFoodRepository userFoodRepository;
	private final UserRepository userRepository;
	private final FoodRepository foodRepository;

	public UserFoodService(UserFoodRepository userFoodRepository, UserRepository userRepository,
			FoodRepository foodRepository) {
		this.userFoodRepository = userFoodRepository;
		this.userRepository = userRepository;
		this.foodRepository = foodRepository;
	}

	@Transactional
	public boolean addUserFood(UserFoodRequest request) {
		User user = userRepository.findByName(request.getUserName());
		Food food = foodRepository.findByName(request.getFoodName());

		if (user == null || food == null)
			return false;

		UserFood userFood = new UserFood();
		userFood.setUser(user);
		userFood.setFood(food);
		userFood.setDate(LocalDate.now());
		userFood.setUnit(request.getUnit());
		userFood.setVolumn(request.getVolumn());
		userFoodRepository.save(userFood);
		return true;
	}
}
