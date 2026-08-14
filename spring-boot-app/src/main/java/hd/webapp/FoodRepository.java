package hd.webapp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
	Food findByName(String name);
	
}
