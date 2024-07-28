package com.simter.domain.member.entity;

import com.simter.domain.airplane.entity.Airplane;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.mail.entity.Mail;
import com.simter.domain.mail.entity.UserMailbox;
import com.simter.domain.solution.entity.CalendarSolution;
import com.simter.domain.solution.entity.Solution;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="user")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 20)
    private String password;

    @Column(length = 20)
    private String nickname;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean hasAirplane;

    @Column(length = 50)
    private String chatbot;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String loginType;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean mailAlert;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean status;

    private LocalDateTime inactiveDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserMailbox> userMailBoxList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChatbotMessage> chatbotMessageList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CounselingLog> counselinglogList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Mail> mailList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Airplane> airplaneList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Solution> solutionList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Calendars> calendarsList = new ArrayList<>();

}
