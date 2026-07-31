package example.webapp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import example.webapp.UserController.AuthRequest;

@RestController
@RequestMapping("/")
public class AuthController {

	private final UserService userService;
	private final ResourceLoader resourceLoader;
	private final JwtService jwtService;
	private final EmailService emailService;
	
	
	private AuthController(UserService userService, ResourceLoader resourceLoader, JwtService jwtService, EmailService emailService) {
		this.userService = userService;
		this.resourceLoader = resourceLoader;
		this.jwtService = jwtService;
		this.emailService = emailService;
	}
	
	@GetMapping("/")
    public String index() {
		System.out.println("++++++++++++++ Welcome to the SSL!  ++++++++++++++");
        return "index";
    }

	
	
	@PostMapping("/api/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request) throws Exception {
		String token = "";
		String url = "";
		// 1. Call your UserService/SecurityService to validate credentials against DB
		if (userService.verifyPassword(request.getName(), request.getPassword())) {
			User user = userService.getUserByName(request.getName());
		    token = jwtService.generateToken(request.getName());
		    if(user.getRole().equalsIgnoreCase("ADMIN"))
		        url = "/admin";
		    else
		        url = "/users/" + user.getId() ;
		    System.out.println("++++++++++ user " + user.getName() + " : " + "Role : " + user.getRole() + " ID : " + user.getId());
		    
            LoginResponse response = new LoginResponse(true, jwtService.generateToken(request.getName()), url, request.getName());
            Gson gson = new Gson();
            // Print the JSON string
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} 
		return new ResponseEntity<>("incorrect username or password", HttpStatus.FORBIDDEN);
	}
	
	@GetMapping("/create")
	public ResponseEntity<String>   addNewUser(@RequestParam("name") String name,  @RequestParam("token") String token) throws Exception {
		System.out.println("------------------------- add new user :  " + name);
		String password = jwtService.extractSubject(token);
		userService.createUser(name, "USER", password);
		System.out.println("------------------------- add new user :  " + name + " done");
		String response = "<html><body>Congratulations " + name + "! <br/>You can login the website through login page <br/> <a href=\"https://192.168.1.81:5173/login\">https://192.168.1.81:5173/login</a>  now<br/> </body></html>";
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/api/signup")
    public ResponseEntity<Map<String, Object>> triggerEmail(@RequestBody SignupRequest request) {
		System.out.println("++++++++++++++ signup " + request.username + " : " + request.password + "++++++++++++++");
		String token = jwtService.generateToken(request.password);
		String text = "You have signup with email address : \n" 
		        + request.username 
				+ "\nPlease confirm it by click on link : \nhttps://192.168.1.81:8443/create?name=" + request.username + "&token=" + token;
		Map<String, Object> response = new HashMap<>();
        try {
            emailService.sendSimpleMessage(request.username, "confirm your email", text);
            response.put("success", true);
            response.put("message", "Email sent successfully");
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
	    // Getters and Setters...
	}
	
	static class AuthRequest {
        private String name;
        private String password;

        // Getters and Setters (required by Spring for JSON deserialization)
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
	}
}
