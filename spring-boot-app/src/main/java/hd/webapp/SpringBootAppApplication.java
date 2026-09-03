package hd.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class SpringBootAppApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringBootAppApplication.class);
	
	private static ApplicationContext applicationContext = null;
	
	public SpringBootAppApplication(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppApplication.class, args);
		
		try {
			for (String name : applicationContext.getBeanDefinitionNames()) {
			    log.info("*****" + name + " ******");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}