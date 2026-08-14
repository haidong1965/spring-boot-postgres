package hd.webapp;

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
		String name = "admin@dietarydiary.com";
		String role = "ADMIN";
		String rawPassword = "rpass"; // <-- CHANGE THIS!
		String hashedPassword = passwordEncoder.encode(rawPassword);
		if (userRepository.findByName("admin@dietarydiary.com") == null) {
			User adminUser = new User(name, role, hashedPassword);
			userRepository.save(adminUser);
		}

		// test user
		String name1 = "test@yahoo.com";
		String role1 = "USER";
		String rawPassword1 = "1"; // <-- CHANGE THIS!
		String hashedPassword1 = passwordEncoder.encode(rawPassword1);
		if (userRepository.findByName("test@yahoo.com") == null) {
			User test = new User(name1, role1, hashedPassword1);
			userRepository.save(test);
		}
		// test mvc user
		String name2 = "mvctest@yahoo.com";
		String role2 = "USER";
		String rawPassword2 = "1"; 
		String hashedPassword2 = passwordEncoder.encode(rawPassword2);
		if (userRepository.findByName("mvctest@yahoo.com") == null) {
			User mvcTest = new User(name2, role2, hashedPassword2);
			userRepository.save(mvcTest);
		}
	}
}