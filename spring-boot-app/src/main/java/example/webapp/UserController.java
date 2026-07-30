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
	
    @GetMapping("/list")
    private ResponseEntity<List<User>> findAll( @RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
        List<User> users = UserRepository.findAll();
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
             token = authHeader.substring(7); 
        }
        if(token != null) {
            if(jwtService.verifyToken(token))
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
    
    
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
            // Call the service layer method, passing the raw password
            User createdUser = userService.createUser(
                    request.username, 
                    request.role,
                    request.password   
            );
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    /*
    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody AuthRequest request) {
    	
    	System.out.println("++++++++++++++ " + request.toString() + "+++++++++++++++++");
        // 1. Call your UserService/SecurityService to validate credentials against DB
        if (userService.verifyPassword(request.name, request.password)) {
            // 2. If valid, generate a JWT token for the user
            String jwtToken = jwtService.generateToken(request.name);
            
            return ResponseEntity.ok("Bearer " + jwtToken); // Return the token!
        } else {
            // 3. If invalid, return 401 Unauthorized
            throw new BadCredentialsException("Invalid credentials");
        }
    }*/
    
    /*
    @GetMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestParam String name, @RequestParam String password) {
        System.out.println("++++++++++++++ Name: " + name + ", Password: " + password + "+++++++++++++++++");
        
        // Call your UserService/SecurityService to validate credentials against DB
        if (userService.verifyPassword(name, password)) {
            // If valid, generate a JWT token for the user
            String jwtToken = jwtService.generateToken(name);
            return ResponseEntity.ok("Bearer " + jwtToken); // Return the token!
        } else {
            // If invalid, return 401 Unauthorized
            throw new BadCredentialsException("Invalid credentials");
        }
    }*/
    
        static class AuthRequest {
            String name;
            String password;
        }

}
