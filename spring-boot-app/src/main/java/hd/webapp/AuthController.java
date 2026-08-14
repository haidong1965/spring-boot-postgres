package hd.webapp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping("/")
public class AuthController {
	
	@Value("${server.port}")
	private String serverPort;
	@Value("${server.public}")
	private String serverPublic;

	private final UserService userService;
	private final JwtService jwtService;
	private final EmailService emailService;
	
	
	private AuthController(UserService userService, ResourceLoader resourceLoader, JwtService jwtService, EmailService emailService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.emailService = emailService;
	}
	
	@GetMapping("/")
    public String index() {
		System.out.println("++++++++++++++ Welcome to the SSL on  ++++++++++++++" + serverPort);
        return "index";
    }

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		System.out.println("++++++++++++++ csrf-token!  ++++++++++++++");
	    return token;
	}
	
	@GetMapping("/validate")
	public ResponseEntity<String> validate(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
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
	public ResponseEntity<String> login(@RequestBody AuthRequest request) throws Exception {
		String url = "";
		System.out.println("++++++++++++++ login   ++++++++++++++");
		if (userService.verifyPassword(request.getName(), request.getPassword())) {
			User user = userService.getUserByName(request.getName());
		    if(user.getRole().equalsIgnoreCase("ADMIN"))
		        url = "/admin";
		    else
		        url = "/users/" + user.getId() ;
		    
            LoginResponse response = new LoginResponse(true, jwtService.generateToken(request.getName()), url, request.getName());
            Gson gson = new Gson();
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} 
		return new ResponseEntity<>("incorrect username or password", HttpStatus.FORBIDDEN);
	}
	
	@GetMapping("/create")
	public ResponseEntity<String>   addNewUser(@RequestParam("name") String name,  @RequestParam("token") String token) throws Exception {
		System.out.println("++++++++++++++ addNewUser!  ++++++++++++++");
		String password = jwtService.extractSubject(token);
		userService.createUser(name, "USER", password);
		String response = "<html><body>Congratulations " + name + "! <br/>You can go to the login page to login  now<br/> </body></html>";
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> triggerEmail(@RequestBody SignupRequest request) {
		System.out.println("++++++++++++++ signup: triggerEmail!  ++++++++++++++");
		String token = jwtService.generateToken(request.password);
		String text = "You have signup with email address : \n" 
		        + request.username 
				+ "\nPlease confirm it by click on link : \nhttps://" + serverPublic + ":" + serverPort + "/create?name=" + request.username + "&token=" + token;
		Map<String, Object> response = new HashMap<>();
        try {
            emailService.sendSimpleMessage(request.username, "confirm your email", text);
            response.put("success", true);
            response.put("message", "Email sent successfully");
            System.out.println("++++++++++++++ signup: Email Sent!  ++++++++++++++");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	response.put("success", false);
            response.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
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

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
	}
}
