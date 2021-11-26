package net.miscthings.messagelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MessagelogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagelogApplication.class, args);
    }

}
