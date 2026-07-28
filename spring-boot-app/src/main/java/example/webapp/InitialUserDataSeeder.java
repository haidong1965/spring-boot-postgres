package example.webapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component // This makes Spring detect this class as a component
public class InitialUserDataSeeder implements CommandLineRunner {

	private final UserRepository userRepository; // Assume you have this repository
	private final PasswordEncoder passwordEncoder;
	private final ResourceLoader resourceLoader;
	private final JwtService jwtService;

	// Constructor Injection (Best Practice)
	public InitialUserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
			ResourceLoader resourceLoader, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.resourceLoader = resourceLoader;
		this.jwtService = jwtService;

	}

	@Override
	public void run(String... args) throws Exception {

		List<User> users = userRepository.findAll();

		for (User user : users) {
			if(user.getName().equalsIgnoreCase("admin")) {
				userRepository.deleteById(user.getId());
			}
		}

		String name = "admin";
		String role = "ADMIN";
		String rawPassword = "rpass"; // <-- CHANGE THIS!

		// CRITICAL: Hash the password securely before saving
		String hashedPassword = passwordEncoder.encode(rawPassword);

		// 3. Create and save the user entity/object
		User adminUser = new User(name, role, hashedPassword);

		String jwtToken = jwtService.generateToken(name);
		System.out.println("++++++++++++++ " + jwtToken + " +++++++++++++++++");
		userRepository.save(adminUser);

		users = userRepository.findAll();

		for (User user : users) {
			System.out.println("--------------------:" + user.toString());
		}

		System.out.println("✅ SUCCESS: Default Admin user " + adminUser.toString());

	}
}