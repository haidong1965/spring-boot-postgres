package hd.webapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {

	private final ObjectMapper objectMapper;
	private final CustomSpringEventPublisher publisher;


	@Value("${server.port}")
	private String serverPort;
	@Value("${server.public}")
	private String serverPublic;

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	public final UserSessionTracker tracker;
	public final UserService userService;
	public final JwtService jwtService;
	public final EmailService emailService;
	public final AuditService auditService;
	private final AuthenticationManager authenticationManager;

	public AuthController(UserService userService, ResourceLoader resourceLoader, JwtService jwtService,
			EmailService emailService, UserSessionTracker tracker, AuditService auditService, ObjectMapper objectMapper,
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
		log.info("**** Welcome to the SSL on " + serverPort + "****");
		return "index";
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		// log.info("**** csrf-token!****");
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

	@GetMapping("/api/auth/activeUsers")
	public ResponseEntity<ArrayList<String>> getActiveUsers() {
		ArrayList<String> response = new ArrayList<String>();
		List<SessionInfo> list = tracker.getAllActiveSessions();
		for (SessionInfo si : list) {
			log.info("****  active session " + si.getUsername());
			response.add(si.getUsername());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/auth/logout")
	public ResponseEntity<String> logout(@RequestBody String username, HttpSession session) throws Exception {
		publisher.publishEvent("user " + username + " logout event");
		log.info("**** logout session ID:" + session.getId() + " ****");
		tracker.unregisterSession(session.getId());
		return new ResponseEntity<>("logout sussessfully.", HttpStatus.OK);
	}

	@PostMapping("/api/auth/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpServletRequest servRequest,
			HttpSession session) throws Exception {
		String url = "";
		
		publisher.publishEvent("user " + request.getName() + " login ");
		
		log.info("**** login session ID:" + session.getId() + " ****");
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
			throws Exception {
		String password = jwtService.extractSubject(token);

		userService.createUser(name, "USER", password);
		auditService.createAudit(
				new CreateAuditRequest("+++++ create request from new User : " + name + " ++++", AuditLevel.INFO));
		String response = "<html><body>Congratulations " + name
				+ "! <br/>You can go to the login page to login  now<br/> </body></html>";
		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> triggerEmail(@RequestBody SignupRequest request) {
		
		publisher.publishEvent("--------- user " + request.username + " signup event -----------");
		
		String token = jwtService.generateToken(request.password);
		String text = "You have signup with email address : \n" + request.username
				+ "\nPlease confirm it by click on link : \nhttps://" + serverPublic + ":" + serverPort
				+ "/create?name=" + request.username + "&token=" + token;
		Map<String, Object> response = new HashMap<>();
		try {
			emailService.sendSimpleMessage(request.username, "confirm your email", text);
			response.put("success", true);
			response.put("message", "Email sent successfully");
			emailService.sendSimpleMessage("hai_dong1@yahoo.com", "new user signup:" + request.username, "+++++++++++++++++++++++");
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
		if (token != null) {
			if (jwtService.verifyToken(token)) {
				userService.deleteUserById(id);
				return ResponseEntity.noContent().build();
			}
		}
		return null;
	}

	/*
	 * @GetMapping("/session/activity") public List<String> getActivity() {
	 * tracker.logActivity("Viewed activity log"); return tracker.getActivityLog();
	 * }
	 */

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
