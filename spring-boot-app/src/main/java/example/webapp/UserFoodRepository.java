package example.webapp;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserFoodRepository extends JpaRepository<UserFood, Long> {
	

}
