package hd.webapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test") 
public class UserServiceTest {
	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	UserService userService;
	
	@Test
	public void testGetUserByName() {
		User tUser = new User("test", "USER", "test");
		given(userRepository.findByName("test")).willReturn(tUser);
		User sUser = userService.getUserByName("test");
		assertTrue(sUser.equals(tUser));
	}

}
