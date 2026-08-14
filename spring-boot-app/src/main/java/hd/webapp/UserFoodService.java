package hd.webapp;

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
	public boolean addUserFood(UserFood userFood) {
		userFood.setDate(LocalDate.now());
		userFoodRepository.save(userFood);
		return true;
	}
}
