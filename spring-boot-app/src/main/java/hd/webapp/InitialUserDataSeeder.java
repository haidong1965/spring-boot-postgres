package hd.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component 
@Profile("!test") 
public class InitialUserDataSeeder implements CommandLineRunner {
	
	
	@Value("#{{environment.OWNER?:'UNKNOWN'}}")
	private String owner;
	
	@Value("#{${jwt.expiration} > 86400000 ? 'Long' : 'Short'}")
	private String expireLevel;
	
	@Value("${password.dummy}")
	private String passwordDummy;
	
	public final Environment env;
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public InitialUserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, Environment env) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.env = env;
	}

	@Override
	public void run(String... args) throws Exception {
		String myVar = env.getProperty("ADMIN_NAME");
		String mypass = env.getProperty("ADMIN_PASSWORD");
		//String name = "admin@dietarydiary.com";
		String role = "ADMIN";
		if (userRepository.findByName(myVar) == null) {
		    String hashedPassword = passwordEncoder.encode(mypass);
			User adminUser = new User(myVar, role, hashedPassword);
			userRepository.save(adminUser);
		}

		// test user
		String name1 = "test@yahoo.com";
		String role1 = "USER";
		String rawPassword1 = passwordDummy; 
		if (userRepository.findByName("test@yahoo.com") == null) {
		    String hashedPassword1 = passwordEncoder.encode(rawPassword1);
			User test = new User(name1, role1, hashedPassword1);
			userRepository.save(test);
		}
		// test mvc user
		String name2 = "mvctest@yahoo.com";
		String role2 = "USER";
		String rawPassword2 = passwordDummy; 
		if (userRepository.findByName("mvctest@yahoo.com") == null) {
		    String hashedPassword2 = passwordEncoder.encode(rawPassword2);
			User mvcTest = new User(name2, role2, hashedPassword2);
			userRepository.save(mvcTest);
		}
		
		String dummy = "dummy";
		String role3 = "USER";
		String rawPassword3 = passwordDummy; 
		if (userRepository.findByName(dummy) == null) {
		    String hashedPassword3 = passwordEncoder.encode(rawPassword3);
			User dummy1 = new User(dummy, role3, hashedPassword3);
			userRepository.save(dummy1);
		}
	}
}