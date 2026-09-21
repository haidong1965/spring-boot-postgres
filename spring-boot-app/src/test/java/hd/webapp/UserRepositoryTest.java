package hd.webapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test") 
class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;
	
	@Test
	void testFindByName() {
		User nUser = new User("test", "USER", "test");
		userRepository.save(nUser);
		User fUser = userRepository.findByName(nUser.getName());
		assertEquals(fUser, nUser);
	}

}
