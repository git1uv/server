package simter.simter.domain;

import jakarta.persistence.*;
import lombok.*;
import simter.simter.domain.enums.Chatbot;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

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
