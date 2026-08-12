package example.webapp;

import org.springframework.stereotype.Service;

@Service
public class FoodService {
	
	private final FoodRepository foodRepository;
	
	 public FoodRepository getFoodRepository() {
		return foodRepository;
	}

	 public FoodService(FoodRepository foodRepository) {
		 this.foodRepository = foodRepository;
	 }
	 
	 public Food getFoodByName(String name) {
	    	Food food = foodRepository.findByName(name);
	        if (food == null) {
	            throw new IllegalArgumentException("Error food name :'" + name + "' not present.");
	        }
	        return food;
	 }
}
