package hd.webapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
class UserController {

	private final JwtService jwtService;
	private final UserService userService;

	private UserController(UserService userService, JwtService jwtService) {
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@GetMapping("/{requestedId}")
	private ResponseEntity<User> findById(@PathVariable Long requestedId) {
		User user = userService.getUserRepository().getReferenceById(requestedId);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/list")
	private ResponseEntity<List<User>> findAll(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
		
		List<User> users = userService.getUserRepository().findAll();
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

	static class UserRequest {
		String username;
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		String role;
		String password;
		String token;
	}

	static class AuthRequest {
		String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		String password;
	}

}
