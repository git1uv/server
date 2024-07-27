package simter.simter;

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
