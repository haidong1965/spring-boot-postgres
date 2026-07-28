package example.webapp;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import example.webapp.UserController.AuthRequest;

@RestController
@RequestMapping("/")
public class AuthController {

	private final UserService userService;
	private final ResourceLoader resourceLoader;
	private final JwtService jwtService;
	
	
	
	private AuthController(UserService userService, ResourceLoader resourceLoader, JwtService jwtService) {
		this.userService = userService;
		this.resourceLoader = resourceLoader;
		this.jwtService = jwtService;
	}
	
	@GetMapping("/")
    public String index() {
		System.out.println("++++++++++++++ Welcome to the SSL!  ++++++++++++++");
        return "index";
    }

	/*
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request) throws Exception {
		System.out.println("++++++++++++++ login ++++++++++++++");
		String token = "";
		// 1. Call your UserService/SecurityService to validate credentials against DB
		if (userService.verifyPassword(request.getName(), request.getPassword())) {
			User user = userService.getUserByName(request.getName());
		    token = jwtService.generateToken(request.getName());
            LoginResponse response = new LoginResponse("true", jwtService.generateToken(request.getName()), "/my-new-page");
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		} 
		return new ResponseEntity<>("wrong username or password", HttpStatus.FORBIDDEN);
	}*/
	
	@PostMapping("/api/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request) throws Exception {
		String token = "";
		String url = "";
		// 1. Call your UserService/SecurityService to validate credentials against DB
		if (userService.verifyPassword(request.getName(), request.getPassword())) {
			User user = userService.getUserByName(request.getName());
		    token = jwtService.generateToken(request.getName());
		    if(user.getRole().equalsIgnoreCase("ADMIN"))
		        url = "/list";
		    else
		        url = "/my-new-page";
            LoginResponse response = new LoginResponse(true, jwtService.generateToken(request.getName()), url, request.getName());
            Gson gson = new Gson();
            // Print the JSON string
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} 
		return new ResponseEntity<>("incorrect username or password", HttpStatus.FORBIDDEN);
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
