package hd.webapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test") 
public class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;
	
	@Test
	public void testFindByName() {
		User nUser = new User("test", "USER", "test");
		userRepository.save(nUser);
		User fUser = userRepository.findByName(nUser.getName());
		assertTrue(fUser.equals(nUser));
	}

}
