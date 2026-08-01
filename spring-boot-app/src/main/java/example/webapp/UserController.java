package example.webapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
class UserController {

	private final UserRepository UserRepository;
	private final UserService userService;
	private final JwtService jwtService;

	private UserController(UserRepository UserRepository, UserService userService, JwtService jwtService) {
		this.UserRepository = UserRepository;
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@GetMapping("/{requestedId}")
	private ResponseEntity<User> findById(@PathVariable Long requestedId) {
		User user = UserRepository.getReferenceById(requestedId);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/list")
	private ResponseEntity<List<User>> findAll(
			@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
		List<User> users = UserRepository.findAll();
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			if (jwtService.verifyToken(token))
				return ResponseEntity.ok(users);
		}
		return null;
	}

	// DTO for receiving clean input data (Good practice!)
	static class UserRequest {
		String username;
		String role;
		String password;
		String token;
	}

	static class AuthRequest {
		String name;
		String password;
	}

}
