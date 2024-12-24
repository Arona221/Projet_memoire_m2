package connect.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "connect.event")
public class ConnectEventBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectEventBackendApplication.class, args);
	}

}
