package com.simter.domain.calendar.entity;


import com.simter.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Calendars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member userId;

    private LocalDate date;

    @Column(length = 100)
    @ColumnDefault("none")
    private String emotion;

    @Column(length = 300)
    private String diary;






}
