package hd.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements InitializingBean, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    
    public UserRepository getUserRepository() {
		return userRepository;
	}

	private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String username, String role,  String rawPassword) {
        // 1. Check for uniqueness (Crucial validation step)
        if (userRepository.findByName(username) != null) {
            throw new IllegalArgumentException("Error: Username '" + username + "' is already taken.");
        }

        // 2. HASH THE PASSWORD using BCrypt
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        // 3. Create the User object with the hashed password
        User newUser = new User();
        newUser.setName(username);
        newUser.setPassword(hashedPassword);
        newUser.setRole(role);
        // 4. Save to the database
        return userRepository.save(newUser);
    }
    
	public User getUserById(Long id) {
    	return userRepository.getUserById(id);
    }
    
    public User getUserByName(String username) {
    	User user = userRepository.findByName(username);
        if (user == null) {
            throw new IllegalArgumentException("Error: Username '" + username + "' not present.");
        }
        return user;
    }
    
    public boolean verifyPassword(String name, String password) {
        User user = userRepository.findByName(name);
        if (user == null) {
            return false; // User does not exist
        }
        // The compare method checks if the provided password matches the stored hash
        return passwordEncoder.matches(password, user.getPassword());
    }

	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
		
	}

	@Override
	public void destroy() throws Exception {
		log.info("************* destroy() ***************");
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("************* afterPropertiesSet() ***************");
	}
}