package com.simter.domain.mail.entity;

import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.member.entity.Member;
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
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member userId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 100)
    private String chatbotType;

    @Column(updatable = false)
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isRead;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL)
    private List<UserMailbox> userMailboxList = new ArrayList<>();



}