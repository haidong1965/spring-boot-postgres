package hd.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class SpringBootAppApplication {

	//private static final Logger log = LoggerFactory.getLogger(SpringBootAppApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppApplication.class, args);
		/*
		try {
			for (String name : applicationContext.getBeanDefinitionNames()) {
			    log.info("*****" + name + " ******");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
}