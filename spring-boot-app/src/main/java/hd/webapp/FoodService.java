package hd.webapp;

import java.util.List;

import org.springframework.data.domain.Sort;
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
	
	public Food getFoodReferenceById(Long id) {
		return foodRepository.getReferenceById(id);
	}
	

	public List<Food> getAllFoodsSortedByName() {
		 return foodRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
	}

	public void deleteById(Long id) {
		foodRepository.deleteById(id);
	}

	public void deleteAll() {
		foodRepository.deleteAll();
	}

	public void add(Food food) {
		foodRepository.save(food);
	}
}
