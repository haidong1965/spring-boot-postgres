package hd.webapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserFoodRepository extends JpaRepository<UserFood, Long> {
	List<UserFood> findByName(String name);

}
