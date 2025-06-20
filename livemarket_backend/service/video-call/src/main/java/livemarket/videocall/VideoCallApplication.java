package livemarket.videocall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "livemarket")
@EnableJpaRepositories(basePackages = "livemarket")
@ComponentScan(basePackages = "livemarket")
public class VideoCallApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoCallApplication.class, args);
    }
}
