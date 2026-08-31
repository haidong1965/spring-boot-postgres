package hd.webapp;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFoodService {

	private static final ZoneId APP_ZONE = ZoneId.of("Europe/Copenhagen");
	private final UserFoodRepository userFoodRepository;
	public UserFoodRepository getUserFoodRepository() {
		return userFoodRepository;
	}

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
		double weight = 1.0;
		if(userFood.getUnit() == 0)
			weight = 100.0;
		
		userFood.setEnergy((int)(foodRepository.getReferenceById(userFood.getFoodId()).getEnergy()*userFood.getVolumn()/weight));
		userFood.setProtein(foodRepository.getReferenceById(userFood.getFoodId()).getProtein()*userFood.getVolumn()/weight);
		userFood.setFiber(foodRepository.getReferenceById(userFood.getFoodId()).getFiber()*userFood.getVolumn()/weight);
		userFood.setCalcium(foodRepository.getReferenceById(userFood.getFoodId()).getCalcium()*userFood.getVolumn()/weight);
		userFood.setName(foodRepository.getReferenceById(userFood.getFoodId()).getName());
		
		userFoodRepository.save(userFood);
		return true;
	}

	public void deleteById(Long id) {
		userFoodRepository.deleteById(id);	
	}
}
