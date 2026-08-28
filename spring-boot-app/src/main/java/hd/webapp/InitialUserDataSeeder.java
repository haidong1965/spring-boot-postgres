package hd.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component 
public class InitialUserDataSeeder implements CommandLineRunner {
	
	@Value("${admin.name}")
    private String adminName;
	@Value("${admin.password}")
    private String adminPassword;
	
	@Value("#{{environment.OWNER?:'UNKNOWN'}}")
	private String owner;
	
	@Value("#{${jwt.expiration} > 86400000 ? 'Long' : 'Short'}")
	private String expireLevel;
	
	@Autowired
    private Environment env;
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public InitialUserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("+++++++++++ Owner name : " + owner);
		System.out.println("+++++++++++ expire level :" + expireLevel);
		
		String myVar = env.getProperty("ADMIN_NAME");
		System.out.println("+++++++++++ admin user name : " + myVar);
		
		//String name = "admin@dietarydiary.com";
		String role = "ADMIN";
		String hashedPassword = passwordEncoder.encode(adminPassword);
		if (userRepository.findByName(adminName) == null) {
			User adminUser = new User(adminName, role, hashedPassword);
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
		
		String dummy = "dummy";
		String role3 = "USER";
		String rawPassword3 = "1"; 
		String hashedPassword3 = passwordEncoder.encode(rawPassword3);
		if (userRepository.findByName(dummy) == null) {
			User dummy1 = new User(dummy, role3, hashedPassword3);
			userRepository.save(dummy1);
		}
	}
}