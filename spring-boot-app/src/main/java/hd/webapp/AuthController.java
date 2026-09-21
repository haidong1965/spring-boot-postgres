package hd.webapp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	private final EmailService emailService;
	private final AuditService auditService;
	private final ObjectMapper objectMapper;
	private final CustomSpringEventPublisher publisher;
	private final UserOperationFacade userOperationFacade;

    AuthController(EmailService emailService,
			AuditService auditService, ObjectMapper objectMapper,
			CustomSpringEventPublisher publisher,
			UserOperationFacade userOperationFacade) {
		this.emailService = emailService;
		this.auditService = auditService;
		this.objectMapper = objectMapper;
		this.publisher = publisher;
		this.userOperationFacade = userOperationFacade;
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
	public ResponseEntity<String> validate(@RequestHeader(name = "Authorization", required = false) String authHeader) 
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
 {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		return userOperationFacade.validate(token);
	}

	@GetMapping("/api/auth/activeUsers")
	public ResponseEntity<List<String>> getActiveUsers() {
		List<String> response = new ArrayList<>();
		List<SessionInfo> list = userOperationFacade.getAllActiveSessions();
		for (SessionInfo si : list) {
			log.info("****  active session {}", si.getUsername());
			response.add(si.getUsername());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/auth/logout")
	public ResponseEntity<String> logout(@RequestBody String username, HttpSession session)  {
		String eventMessage = String.format("User %s logout event", username);
		publisher.publishEvent(eventMessage);
		log.info("logout session ID: {}", session.getId());
		userOperationFacade.unregisterSession(session.getId());
		return new ResponseEntity<>("logout sussessfully.", HttpStatus.OK);
	}

	@PostMapping("/api/auth/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpServletRequest servRequest,
			HttpSession session) throws JsonProcessingException  {
		String messageEvent = String.format("User %s login", request.getName());
		publisher.publishEvent(messageEvent);
		
		log.info("login session ID: {}", session.getId());
		if(userOperationFacade.verifyPassword(request.getName(), request.getPassword())) {
			return new ResponseEntity<>(objectMapper.writeValueAsString(userOperationFacade.login(request, servRequest, session)), HttpStatus.OK);
		}
		
		return new ResponseEntity<>("incorrect username or password", HttpStatus.FORBIDDEN);
	}

	@GetMapping("/create")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
	public ResponseEntity<String> addNewUser(@RequestParam("name") String name, @RequestParam("token") String token)
			throws IllegalArgumentException {
		String createMessage = String.format("+++++ create request from new User : %s ++++", name);
		auditService.createAudit(new CreateAuditRequest(createMessage, AuditLevel.INFO));
		return userOperationFacade.addNewUser(name, token);
	}

	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> triggerEmail(@RequestBody SignupRequest request) {
		String eventMessage = String.format("User %s signup event",  request.username);
		publisher.publishEvent(eventMessage);
		
		String token = userOperationFacade.generateToken(request.password);
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
			@PathVariable Long id) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return userOperationFacade.deleteUser(authHeader, id);
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
