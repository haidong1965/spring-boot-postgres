package hd.webapp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFoodService {

	private static final Logger log = LoggerFactory.getLogger(UserFoodService.class);
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
		log.info("xxxxxxxxxxxxxxxx " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString() + " xxxxxxxxxxxxxxxxxx");
		userFoodRepository.save(userFood);
		return true;
	}

	@Transactional
	public void deleteById(Long id) {
		userFoodRepository.deleteById(id);	
	}

	@Transactional
	//@Transactional(propagation = Propagation.NESTED)
    public void deleteAllByUserId(@Param("userId") Long userId) {
		userFoodRepository.deleteAllByUserId(userId);
	}

	public List<UserFood> findAll() {
		// TODO Auto-generated method stub
		return userFoodRepository.findAll();
	}

	public void add(UserFood uf) {
		userFoodRepository.save(uf);
	}
}
