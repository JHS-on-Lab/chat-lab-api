package me.son.chatlabapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChatLabApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatLabApiApplication.class, args);
    }

}
