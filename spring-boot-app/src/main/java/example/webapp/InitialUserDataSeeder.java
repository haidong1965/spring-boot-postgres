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
		if(userRepository.findByName("admin@dietarydiary.com") != null)
			return;
		String name = "admin@dietarydiary.com";
		String role = "ADMIN";
		String rawPassword = "rpass"; // <-- CHANGE THIS!
		String hashedPassword = passwordEncoder.encode(rawPassword);
		User adminUser = new User(name, role, hashedPassword);
		userRepository.save(adminUser);
		System.out.println("✅ SUCCESS: Default Admin user " + adminUser.toString());
	}
}