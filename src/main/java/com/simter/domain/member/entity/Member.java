package com.simter.domain.member.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;


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


}
