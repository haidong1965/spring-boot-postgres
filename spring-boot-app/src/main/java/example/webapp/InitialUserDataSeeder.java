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
		//test user
		String name1 = "hai_dong1@yahoo.com";
		String role1 = "USER";
		String rawPassword1 = "1"; // <-- CHANGE THIS!
		String hashedPassword1 = passwordEncoder.encode(rawPassword1);
		User hdo = new User(name1, role1, hashedPassword1);
		userRepository.save(hdo);
	}
}