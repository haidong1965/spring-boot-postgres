package hd.webapp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/")
public class AuthController {

	@Value("${server.port}")
	private String serverPort;
	@Value("${server.public}")
	private String serverPublic;
	
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	public final UserSessionTracker tracker;
	public final UserService userService;
	public final JwtService jwtService;
	public final EmailService emailService;

	public AuthController(UserService userService, ResourceLoader resourceLoader, JwtService jwtService,
			EmailService emailService, UserSessionTracker tracker) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.emailService = emailService;
		this.tracker = tracker;
	}

	@GetMapping("/")
	public String index() {
		log.info("**** Welcome to the SSL on " + serverPort + "****");
		return "index";
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		log.info("**** csrf-token!****");
		return token;
	}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(@RequestHeader(name = "Authorization", required = false) String authHeader)
			throws Exception {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			if (jwtService.verifyToken(token))
				return ResponseEntity.ok("");
		}
		return null;
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpSession session) throws Exception {

		String url = "";
		log.info("**** login session ID:" + session.getId() + " ****");
		if (userService.verifyPassword(request.getName(), request.getPassword())) {
			User user = userService.getUserByName(request.getName());
			if (user.getRole().equalsIgnoreCase("ADMIN"))
				url = "/admin";
			else
				url = "/users/" + user.getId();
			
			tracker.setSessionId(session.getId());
			tracker.setUsername(user.getName());
			tracker.setLoginTime(LocalDateTime.now());
			tracker.logActivity("User logged in");

			LoginResponse response = new LoginResponse(true, jwtService.generateToken(request.getName()), url,
					request.getName());
			Gson gson = new Gson();
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}
		return new ResponseEntity<>("incorrect username or password", HttpStatus.FORBIDDEN);
	}

	@GetMapping("/create")
	public ResponseEntity<String> addNewUser(@RequestParam("name") String name, @RequestParam("token") String token)
			throws Exception {
		System.out.println("++++++++++++++ addNewUser!  ++++++++++++++");
		String password = jwtService.extractSubject(token);
		userService.createUser(name, "USER", password);
		String response = "<html><body>Congratulations " + name
				+ "! <br/>You can go to the login page to login  now<br/> </body></html>";
		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> triggerEmail(@RequestBody SignupRequest request) {
		String token = jwtService.generateToken(request.password);
		String text = "You have signup with email address : \n" + request.username
				+ "\nPlease confirm it by click on link : \nhttps://" + serverPublic + ":" + serverPort
				+ "/create?name=" + request.username + "&token=" + token;
		Map<String, Object> response = new HashMap<>();
		try {
			emailService.sendSimpleMessage(request.username, "confirm your email", text);
			response.put("success", true);
			response.put("message", "Email sent successfully");
			emailService.sendSimpleMessage("hai_dong1@yahoo.com", "new user signup:" + request.username, "+++++++++++++++++++++++++");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Failed to send email: " + e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteUser(@RequestHeader(name = "Authorization", required = false) String authHeader,
			@PathVariable Long id) throws Exception {
		System.out.println("++++++++++++++ deleteUser " + id + " ++++++++++++++");
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token != null) {
			if (jwtService.verifyToken(token)) {
				userService.deleteUserById(id);
				return ResponseEntity.noContent().build();
			}
		}
		return null;
	}

	@GetMapping("/session/activity")
	public List<String> getActivity() {
		tracker.logActivity("Viewed activity log");
		return tracker.getActivityLog();
	}

	static class SignupRequest {
		private String username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		private String password;
	}

	static class AuthRequest {
		private String name;
		private String password;

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
	}
}
