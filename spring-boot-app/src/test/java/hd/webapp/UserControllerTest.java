package hd.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class) 
@ActiveProfiles("test") 
public class UserControllerTest {

	@Autowired
    private MockMvc mockMvc; // The tool to simulate HTTP requests
	@MockitoBean // Creates a mock of UserService and injects it into UserController
    private UserService userService;
	
	@MockitoBean
    private JwtService jwtService;
	
	@Test
	void testFindById() throws Exception {
		User mockUser = new User("test", "USER", "test");
	    mockUser.setId(1L);
		given(userService.getUserReferenceById(1L)).willReturn(mockUser);
		mockMvc.perform(get("/users/1"))
        .andExpect(status().isOk())               // HTTP 200
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("test"));
	}
	
}
