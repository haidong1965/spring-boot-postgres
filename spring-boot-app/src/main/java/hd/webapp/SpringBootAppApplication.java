package hd.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class SpringBootAppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppApplication.class, args);
	}
	
}