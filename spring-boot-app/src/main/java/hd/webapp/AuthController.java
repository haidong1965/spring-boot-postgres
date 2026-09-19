package hd.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {

	


	@Value("${server.port}")
	private String serverPort;
	@Value("${server.public}")
	private String serverPublic;

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	
	private final UserService userService;
	private final JwtService jwtService;
	private final EmailService emailService;
	private final UserSessionTracker tracker;
	private final AuditService auditService;
	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final CustomSpringEventPublisher publisher;

    AuthController(UserService userService, JwtService jwtService,
			EmailService emailService, UserSessionTracker tracker, 
			AuditService auditService, ObjectMapper objectMapper,
			AuthenticationManager authenticationManager, CustomSpringEventPublisher publisher) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.emailService = emailService;
		this.tracker = tracker;
		this.auditService = auditService;
		this.objectMapper = objectMapper;
		this.authenticationManager = authenticationManager;
		this.publisher = publisher;
	}

	@GetMapping("/")
	public String index() {
		log.info("SSL on {} ", serverPort);
		return "index";
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if ((token != null) && jwtService.verifyToken(token))
		    return ResponseEntity.ok("");
	
		return null;
	}

	@GetMapping("/api/auth/activeUsers")
	public ResponseEntity<List<String>> getActiveUsers() {
		List<String> response = new ArrayList<>();
		List<SessionInfo> list = tracker.getAllActiveSessions();
		for (SessionInfo si : list) {
			log.info("****  active session " + si.getUsername());
			response.add(si.getUsername());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/auth/logout")
	public ResponseEntity<String> logout(@RequestBody String username, HttpSession session)  {
		String eventMessage = String.format("User %s logout event", username);
		publisher.publishEvent(eventMessage);
		log.info("logout session ID: {}", session.getId());
		tracker.unregisterSession(session.getId());
		return new ResponseEntity<>("logout sussessfully.", HttpStatus.OK);
	}

	@PostMapping("/api/auth/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpServletRequest servRequest,
			HttpSession session) throws JsonProcessingException  {
		String url = "";
		String messageEvent = String.format("User %s login", request.getName());
		publisher.publishEvent(messageEvent);
		
		log.info("login session ID: {}", session.getId());
		if (userService.verifyPassword(request.getName(), request.getPassword())) {
			Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword()));
			
			String token = jwtService.generateToken(authentication.getName());
			SecurityContextHolder.getContext().setAuthentication(authentication);

			User user = userService.getUserByName(request.getName());
			if (user.getRole().equalsIgnoreCase("ADMIN"))
				url = "/admin";
			else
				url = "/users/" + user.getId();

			tracker.registerSession(session.getId(), authentication.getName(), getClientIp(servRequest),
					servRequest.getHeader("User-Agent"));
			LoginResponse response = new LoginResponse(true, token, url, request.getName());
			return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);
		}
		return new ResponseEntity<>("incorrect username or password", HttpStatus.FORBIDDEN);
	}

	@GetMapping("/create")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
	public ResponseEntity<String> addNewUser(@RequestParam("name") String name, @RequestParam("token") String token)
			throws IllegalArgumentException {
		String password = jwtService.extractSubject(token);

		userService.createUser(name, "USER", password);
		String createMessage = String.format("+++++ create request from new User : %s ++++", name);
		auditService.createAudit(
				new CreateAuditRequest(createMessage, AuditLevel.INFO));
		String response = String.format("<html><body>Congratulations %s! <br/>You can go to the login page to login  now<br/> </body></html>", name);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> triggerEmail(@RequestBody SignupRequest request) {
		String eventMessage = String.format("User %s signup event",  request.username);
		publisher.publishEvent(eventMessage);
		
		String token = jwtService.generateToken(request.password);
		String text = String.format("You have signup with email address : %n %s%nPlease confirm it by click on link : %nhttps://%s:%s/create?name=%s&token=%s", 
				request.username, serverPublic, serverPort, request.username, token);
		Map<String, Object> response = new HashMap<>();
		try {
			emailService.sendSimpleMessage(request.username, "confirm your email", text);
			response.put("success", true);
			response.put("message", "Email sent successfully");
			String simpleMessage = String.format("new user signup:%s", request.username);
			emailService.sendSimpleMessage("hai_dong1@yahoo.com", simpleMessage, "+++++++++++++++++++++++");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Failed to send email: " + e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@DeleteMapping("/delete/{id}")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
	public ResponseEntity<Void> deleteUser(@RequestHeader(name = "Authorization", required = false) String authHeader,
			@PathVariable Long id) throws Exception {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if ((token != null) && jwtService.verifyToken(token)) {
			userService.deleteUserById(id);
			return ResponseEntity.noContent().build();
		}
		
		return null;
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

	public static String getClientIp(HttpServletRequest request) {
		String[] headers = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
				"HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
				"HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

		for (String header : headers) {
			String ip = request.getHeader(header);
			if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
				// X-Forwarded-For can contain multiple IPs; take the first
				return ip.split(",")[0].trim();
			}
		}

		return request.getRemoteAddr();
	}
}
