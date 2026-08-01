package example.webapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component // This makes Spring detect this class as a component
public class InitialUserDataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public InitialUserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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
	}
}