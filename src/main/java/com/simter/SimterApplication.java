package com.simter;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimterApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();

        System.setProperty("DB_ROOTPW", dotenv.get("DB_ROOTPW"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PW", dotenv.get("DB_PW"));

        SpringApplication.run(SimterApplication.class, args);
	}

    @Entity
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Member {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long user_id;

        private String password;

        private String nickname;

        private Boolean has_airplane = false;

        private String chatbot;

        private String email;

        private String login_type;

        private String status;

        private String inactive_data;

        private String latest_chatbot;

        private Boolean mail_alert = false;


    }
}
