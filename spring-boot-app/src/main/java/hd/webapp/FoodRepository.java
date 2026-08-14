package hd.webapp;

import java.util.List;
import org.springframework.data.domain.Sort; 
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
	Food findByName(String name);
	List<Food> findAll(Sort sort);
}
