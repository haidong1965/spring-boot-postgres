package hd.webapp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import hd.webapp.AuthController.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UserOperationFacade {

	private final UserService userService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UserSessionTracker tracker;

	UserOperationFacade(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager,
			UserSessionTracker tracker) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.tracker = tracker;
	}

	public ResponseEntity<String> validate(String token)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		if ((token != null) && jwtService.verifyToken(token))
			return ResponseEntity.ok("");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token");
	}

	public LoginResponse login(AuthRequest request, 
			HttpServletRequest servRequest,
			HttpSession session) {
		String url = "";
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword()));

		String token = jwtService.generateToken(authentication.getName());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		User user = userService.getUserByName(request.getName());
		if (user.getRole().equalsIgnoreCase("ADMIN"))
			url = "/admin";
		else
			url = "/users/" + user.getId();

		tracker.registerSession(session.getId(), authentication.getName(), getClientIp(servRequest),
				servRequest.getHeader("User-Agent"));
		return new LoginResponse(true, token, url, request.getName());
	}
	
	public ResponseEntity<String> addNewUser(String name, String token)
			throws IllegalArgumentException {
		String password = jwtService.extractSubject(token);

		userService.createUser(name, "USER", password);
		
		String response = String.format("<html><body>Congratulations %s! <br/>You can go to the login page to login  now<br/> </body></html>", name);
		return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<Void> deleteUser(String authHeader, Long id) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if ((token != null) && jwtService.verifyToken(token)) {
			userService.deleteUserById(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build(); 
	}
	
	public String generateToken(String subject) {
		return jwtService.generateToken(subject);
	}

	public boolean verifyPassword(String name, String password) {
		return userService.verifyPassword(name, password);
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
	
	public List<SessionInfo> getAllActiveSessions() {
		return tracker.getAllActiveSessions();
	}
	
	public void unregisterSession(String sessionId) {
	    tracker.unregisterSession(sessionId);
	}
}
