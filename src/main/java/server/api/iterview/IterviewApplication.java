package server.api.iterview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IterviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(IterviewApplication.class, args);
	}

}
