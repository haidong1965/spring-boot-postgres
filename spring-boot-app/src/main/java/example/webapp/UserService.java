package example.webapp;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Dependency Injection: Spring automatically provides implementations for these interfaces
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        System.out.println("++++++++++++++  UserService +++++++++++++++++");
    }

    /**
     * Creates a new user account securely by hashing the provided password.
     * @param username The desired unique username.
     * @param rawPassword The plaintext password provided by the user during sign-up.
     * @param email User's email.
     * @return The saved User entity.
     * @throws IllegalArgumentException if the username already exists.
     */
    public User createUser(String username, String role,  String rawPassword) {
    	System.out.println("++++++++++++++  UserService::createUser +++++++++++++++++");
        // 1. Check for uniqueness (Crucial validation step)
        if (userRepository.findByName(username) != null) {
            throw new IllegalArgumentException("Error: Username '" + username + "' is already taken.");
        }

        // 2. HASH THE PASSWORD using BCrypt
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        // 3. Create the User object with the hashed password
        User newUser = new User();
        newUser.setName(username);
        newUser.setPassword(hashedPassword); // <-- Storing the hash!
        newUser.setRole(role);
        System.out.println("++++++++++++++ " + newUser.toString() + "+++++++++++++++++");
        // 4. Save to the database
        return userRepository.save(newUser);
    }
    
    public User getUserByName(String username) {
    	System.out.println("++++++++++++++  UserService::getUserByName +++++++++++++++++");
        // 1. Check for uniqueness (Crucial validation step)
    	User user = userRepository.findByName(username);
        if (user == null) {
            throw new IllegalArgumentException("Error: Username '" + username + "' not present.");
        }
        return user;
    }
    
    /**
     * Optional: Helper method to check a password later during login validation.
     */
    
    public boolean verifyPassword(String name, String password) {
    	System.out.println("++++++++++++++ " + name + "+++++++++++++++++" +  password);
        User user = userRepository.findByName(name);
        if (user == null) {
            return false; // User does not exist
        }
        
        System.out.println("++++++++++++++ user data: " + user.getName() + " +++++++++++++++++" +  user.getPassword());
        // The compare method checks if the provided password matches the stored hash
        return passwordEncoder.matches(password, user.getPassword());
    }
}