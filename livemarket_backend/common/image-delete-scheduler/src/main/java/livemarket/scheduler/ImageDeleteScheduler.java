package livemarket.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "livemarket.scheduler.batch",
        "livemarket.articleImages.service",
        "livemarket.articleImages.repository"
})
@EnableScheduling
@EnableJpaRepositories(basePackages = "livemarket.articleImages.repository")
@EntityScan(basePackages = "livemarket.articleImages.entity")
public class ImageDeleteScheduler {
    public static void main(String[] args) {
        SpringApplication.run(ImageDeleteScheduler.class, args);
    }
}
