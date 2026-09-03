package hd.webapp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFoodService {

	private final UserFoodRepository userFoodRepository;
	public UserFoodRepository getUserFoodRepository() {
		return userFoodRepository;
	}

	public UserFoodService(UserFoodRepository userFoodRepository, UserRepository userRepository,
			FoodRepository foodRepository) {
		this.userFoodRepository = userFoodRepository;
	}

	@Transactional
	public boolean addUserFood(UserFood userFood) {
		userFoodRepository.save(userFood);
		return true;
	}

	@Transactional
	public void deleteById(Long id) {
		userFoodRepository.deleteById(id);	
	}
}
